"""
Conditional Routing Workflow

  User message
      │
      ▼
  [classify_intent]  ← LLM classifies intent
      │
      ├─── nutrition → [handle_nutrition]
      ├─── fitness   → [handle_fitness]
      ├─── mental    → [handle_mental]
      ├─── metrics   → [handle_metrics]
      └─── general   → [handle_general]
      │
      ▼
   reply

Author: Htet Nandar
"""

from __future__ import annotations

import json
import os
from typing import TypedDict, Optional, Literal

from langchain_openai import ChatOpenAI
from openai import OpenAI
from langgraph.graph import StateGraph, START, END

from services.wellness_tools import TOOLS, TOOL_REGISTRY

# ── LLM config ────────────────────────────────────────────────────────────────

OPENROUTER_BASE = "https://openrouter.ai/api/v1"
CHAT_MODEL      = "openai/gpt-4o-mini"
MAX_ITERATIONS  = 5


# ── Shared workflow state ──────────────────────────────────────────────────────

class WellnessState(TypedDict):
    message:      str
    history:      list
    user_context: Optional[dict]
    route:        str
    reply:        str


# ── Tools per specialist ───────────────────────────────────────────────────────

ROUTE_TOOLS = {
    "nutrition": ["search_wellness_knowledge", "calculate_daily_calories"],
    "fitness":   ["search_wellness_knowledge"],
    "mental":    ["search_wellness_knowledge"],
    "metrics":   ["search_wellness_knowledge", "calculate_bmi", "calculate_daily_calories"],
    "general":   ["search_wellness_knowledge", "calculate_bmi", "calculate_daily_calories"],
}

# ── Specialist system prompts ──────────────────────────────────────────────────

SYSTEM_PROMPTS = {
                    "nutrition": """\
                You are a Nutrition & Diet specialist for the Wellness app.
                
                Your focus: meal planning, calorie intake, macronutrients, healthy eating habits,
                food choices, dietary goals, and weight management through diet.
                
                Tools available:
                - search_wellness_knowledge — search the evidence-based wellness knowledge base
                - calculate_daily_calories  — estimate daily calorie needs (TDEE)
                
                Guidelines:
                - Always call search_wellness_knowledge before giving dietary advice.
                - Call calculate_daily_calories when the user provides weight, height, age, and activity level.
                - Keep answers practical, specific, and motivating.
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,

                    "fitness": """\
                You are a Fitness & Exercise specialist for the Wellness app.

                Your focus: workout plans, physical activity, step goals, strength training,
                cardio, flexibility, recovery, and sports performance.

                Tools available:
                - search_wellness_knowledge — search the evidence-based wellness knowledge base

                Guidelines:
                - Always call search_wellness_knowledge before recommending any exercise programme.
                - Provide specific, actionable advice (sets, reps, duration, frequency where relevant).
                - Adapt to the user's fitness level if they mention it.
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,

                    "mental": """\
                You are a Mental Wellness specialist for the Wellness app.

                Your focus: stress management, sleep hygiene, mood improvement, mindfulness,
                anxiety coping strategies, emotional wellbeing, and relaxation techniques.

                Tools available:
                - search_wellness_knowledge — search the evidence-based wellness knowledge base

                Guidelines:
                - Be empathetic and supportive — the user may be in a vulnerable state.
                - Always call search_wellness_knowledge to ground your advice in evidence.
                - Provide practical, step-by-step strategies the user can act on immediately.
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,

                    "metrics": """\
                You are a Health Metrics specialist for the Wellness app.

                Your focus: BMI calculation, healthy weight ranges, daily calorie needs (TDEE),
                hydration targets, and interpreting personal health numbers.

                Tools available:
                - calculate_bmi             — ALWAYS use when the user provides weight + height
                - calculate_daily_calories  — ALWAYS use when the user asks about calorie targets
                - search_wellness_knowledge — use for context on what the numbers mean

                Guidelines:
                - Never estimate BMI or TDEE manually — always call the calculator tools.
                - After calculating, explain what the result means and what action to take.
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,

                    "general": """\
                You are a friendly, knowledgeable wellness assistant for the Wellness app.

                You help users with any wellness topic: nutrition, fitness, sleep, mental health,
                hydration, healthy lifestyle habits, and general health questions.

                Tools available:
                - search_wellness_knowledge — search the evidence-based wellness knowledge base
                - calculate_bmi             — compute BMI from weight and height
                - calculate_daily_calories  — estimate daily calorie needs (TDEE)

                Guidelines:
                - Use search_wellness_knowledge for factual wellness questions.
                - Use calculate_bmi / calculate_daily_calories when the user provides measurements.
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,
                }


# ── Shared agent-loop helper (called by each handle_* function) ────────────────

def _run_agent(route: str, state: WellnessState, client: OpenAI) -> dict:
    """Run the specialist agent loop with filtered tools for the given route."""
    allowed      = set(ROUTE_TOOLS[route])
    spec_tools   = [t for t in TOOLS if t["function"]["name"] in allowed]
    system_prompt = SYSTEM_PROMPTS[route]

    # Optionally inject user profile
    content = system_prompt
    if state.get("user_context"):
        profile = "\n".join(f"  {k}: {v}" for k, v in state["user_context"].items())
        content += f"\n\nUser profile (use when relevant):\n{profile}"

    messages = [{"role": "system", "content": content}]

    for msg in state.get("history") or []:
        messages.append(msg if isinstance(msg, dict) else {"role": msg.role, "content": msg.content})

    messages.append({"role": "user", "content": state["message"]})

    for _ in range(MAX_ITERATIONS):
        response = client.chat.completions.create(
            model=CHAT_MODEL,
            messages=messages,
            tools=spec_tools if spec_tools else None,
            tool_choice="auto" if spec_tools else "none",
            temperature=0.7,
        )
        assistant_msg = response.choices[0].message

        if not assistant_msg.tool_calls:
            return {"reply": assistant_msg.content}

        messages.append(assistant_msg)

        for tc in assistant_msg.tool_calls:
            fn = tc.function.name
            try:
                result = TOOL_REGISTRY[fn](**json.loads(tc.function.arguments)) if fn in allowed else f"Tool '{fn}' not available."
                tool_result = result if isinstance(result, str) else json.dumps(result)
            except Exception as e:
                tool_result = json.dumps({"error": str(e)})
            messages.append({"role": "tool", "tool_call_id": tc.id, "content": tool_result})

    # Budget exhausted
    messages.append({"role": "user", "content": "Please give your final answer based on what you've found."})
    fallback = client.chat.completions.create(model=CHAT_MODEL, messages=messages, temperature=0.7)
    return {"reply": fallback.choices[0].message.content}


# ── Workflow factory ───────────────────────────────────────────────────────────

def build_wellness_workflow(api_key: str):
    # Course-style LLM for classify_intent
    llm = ChatOpenAI(
        model=CHAT_MODEL,
        api_key=api_key,
        base_url=OPENROUTER_BASE,
        temperature=0,
    )

    # Raw OpenAI client for specialist agent loops (tool-calling)
    client = OpenAI(base_url=OPENROUTER_BASE, api_key=api_key)

    # ── Router node (matches course: classify_intent) ─────────────────────────
    def classify_intent(state: WellnessState) -> dict:
        response = llm.invoke(
                            f"""Classify this wellness message into exactly one category:
                            
                            nutrition : diet, food, calories, eating, meal planning, macros, weight loss through diet
                            fitness   : exercise, workouts, steps, physical activity, training, gym, running
                            mental    : sleep, stress, mood, anxiety, mindfulness, mental health, relaxation
                            metrics   : BMI, body mass index, calorie calculation, TDEE, weight/height numbers
                            general   : anything that does not clearly fit the categories above
                            
                            Message: {state["message"]}
                            
                            Return only one word.
                            """)
        raw = response.content.strip().lower()
        route = raw if raw in ROUTE_TOOLS else "general"
        return {"route": route}

    # ── Route selector (matches course: route_decision) ───────────────────────
    def route_decision(state: WellnessState) -> Literal["nutrition", "fitness", "mental", "metrics", "general"]:
        return state["route"]

    # ── Specialist handlers (matches course: handle_technical / handle_billing / handle_general) ──
    def handle_nutrition(state: WellnessState) -> dict:
        return _run_agent("nutrition", state, client)

    def handle_fitness(state: WellnessState) -> dict:
        return _run_agent("fitness", state, client)

    def handle_mental(state: WellnessState) -> dict:
        return _run_agent("mental", state, client)

    def handle_metrics(state: WellnessState) -> dict:
        return _run_agent("metrics", state, client)

    def handle_general(state: WellnessState) -> dict:
        return _run_agent("general", state, client)

    # ── Graph assembly ───────────
    builder = StateGraph(WellnessState)

    builder.add_node("classify_intent", classify_intent)
    builder.add_node("nutrition", handle_nutrition)
    builder.add_node("fitness",   handle_fitness)
    builder.add_node("mental",    handle_mental)
    builder.add_node("metrics",   handle_metrics)
    builder.add_node("general",   handle_general)

    builder.add_edge(START, "classify_intent")

    builder.add_conditional_edges(
        "classify_intent",
        route_decision,
        {
            "nutrition": "nutrition",
            "fitness":   "fitness",
            "mental":    "mental",
            "metrics":   "metrics",
            "general":   "general",
        }
    )

    builder.add_edge("nutrition", END)
    builder.add_edge("fitness",   END)
    builder.add_edge("mental",    END)
    builder.add_edge("metrics",   END)
    builder.add_edge("general",   END)

    return builder.compile()
