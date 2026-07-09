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

from openai import OpenAI
from langgraph.graph import StateGraph, START, END

from langchain_core.utils.function_calling import convert_to_openai_function

# ── MCP tool helpers ──────────────────────────────────────────────────────────

def _to_openai_tools(lc_tools: list) -> list:
    """Convert a list of LangChain BaseTool objects to OpenAI function-calling schemas."""
    result = []
    for tool in lc_tools:
        schema = convert_to_openai_function(tool)
        result.append({"type": "function", "function": schema})
    return result


# ── LLM config ────────────────────────────────────────────────────────────────

OPENROUTER_BASE        = "https://openrouter.ai/api/v1"
CHAT_MODEL_OPENROUTER  = "openai/gpt-4o-mini"   # OpenRouter format
CHAT_MODEL_OPENAI      = "gpt-4o-mini"           # Direct OpenAI format
MAX_ITERATIONS         = 3


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
                - Call search_wellness_knowledge ONCE, then answer immediately using the results.
                - Call calculate_daily_calories ONCE if the user provides weight, height, age, and activity level.
                - Do NOT call search_wellness_knowledge more than once.
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
                - Call search_wellness_knowledge ONCE, then answer immediately using the results.
                - Do NOT call search_wellness_knowledge more than once.
                - Provide specific, actionable advice (sets, reps, duration, frequency where relevant).
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,

    "mental": """\
                You are a Mental Wellness specialist for the Wellness app.

                Your focus: stress management, sleep hygiene, mood improvement, mindfulness,
                anxiety coping strategies, emotional wellbeing, and relaxation techniques.

                Tools available:
                - search_wellness_knowledge — search the evidence-based wellness knowledge base

                Guidelines:
                - Call search_wellness_knowledge ONCE, then answer immediately using the results.
                - Do NOT call search_wellness_knowledge more than once.
                - Be empathetic and supportive — the user may be in a vulnerable state.
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
                - search_wellness_knowledge — use ONCE for context on what the numbers mean

                Guidelines:
                - Never estimate BMI or TDEE manually — always call the calculator tools.
                - Call each tool ONCE only. Do not repeat tool calls.
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
                - Call search_wellness_knowledge ONCE only, then answer immediately using the results.
                - Call calculate_bmi / calculate_daily_calories ONCE if the user provides measurements.
                - Do NOT repeat any tool call.
                - IMPORTANT: Reply in 2–3 sentences maximum.
                """,
}


# ── Shared agent-loop helper (called by each handle_* function) ────────────────

def _run_agent(
        route: str,
        state: WellnessState,
        client: OpenAI,
        model: str,
        openai_tools: list,
        tool_registry: dict,
) -> dict:
    """Run the specialist agent loop with MCP tools filtered for the given route."""
    allowed    = set(ROUTE_TOOLS[route])
    spec_tools = [t for t in openai_tools if t["function"]["name"] in allowed]
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

    called_tools: set[str] = set()   # prevent duplicate tool calls

    for _ in range(MAX_ITERATIONS):
        response = client.chat.completions.create(
            model=model,
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
                if fn in called_tools:
                    # Already called this tool — return cached result notice so LLM stops looping
                    print(f"[Tool]   skipping duplicate call to {fn}")
                    tool_result = "Already called. Use the result from the previous call to answer."
                elif fn in allowed and fn in tool_registry:
                    called_tools.add(fn)  # mark BEFORE invoke so parallel/retry calls are blocked
                    print(f"[Tool]   calling {fn}({tc.function.arguments})")
                    result = tool_registry[fn].invoke(json.loads(tc.function.arguments))
                    tool_result = result if isinstance(result, str) else json.dumps(result)
                    print(f"[Tool]   {fn} → done")
                else:
                    tool_result = f"Tool '{fn}' not available."
            except Exception as e:
                tool_result = json.dumps({"error": str(e)})
            messages.append({"role": "tool", "tool_call_id": tc.id, "content": tool_result})

    # Budget exhausted
    messages.append({"role": "user", "content": "Please give your final answer based on what you've found."})
    fallback = client.chat.completions.create(model=model, messages=messages, temperature=0.7)
    return {"reply": fallback.choices[0].message.content}


# ── Workflow factory ───────────────────────────────────────────────────────────

def build_wellness_workflow(api_key: str, base_url: str | None = None, mcp_tools: list = None):
    # Single raw OpenAI client for both classify_intent and specialist agent loops.
    # Works with OpenRouter (base_url=OPENROUTER_BASE) and direct OpenAI (base_url=None).
    client = OpenAI(
        api_key=api_key,
        base_url=base_url,   # None = use OpenAI default; set to OPENROUTER_BASE for OpenRouter
    )
    # OpenRouter requires "openai/gpt-4o-mini"; direct OpenAI requires "gpt-4o-mini"
    model = CHAT_MODEL_OPENROUTER if base_url else CHAT_MODEL_OPENAI

    # Convert MCP LangChain tools → OpenAI schemas + lookup dict
    openai_tools  = _to_openai_tools(mcp_tools or [])
    tool_registry = {t.name: t for t in (mcp_tools or [])}
    print(f"[Workflow] MCP tools loaded: {list(tool_registry.keys())}")

    # ── Router node (matches course: classify_intent) ─────────────────────────
    def classify_intent(state: WellnessState) -> dict:
        response = client.chat.completions.create(
            model=model,
            messages=[{
                "role": "user",
                "content": f"""Classify this wellness message into exactly one category:

                nutrition : diet, food, calories, eating, meal planning, macros, weight loss through diet
                fitness   : exercise, workouts, steps, physical activity, training, gym, running
                mental    : sleep, stress, mood, anxiety, mindfulness, mental health, relaxation
                metrics   : BMI, body mass index, calorie calculation, TDEE, weight/height numbers
                general   : anything that does not clearly fit the categories above
                
                Message: {state["message"]}
                
                Return only one word."""
            }],
            temperature=0,
        )
        raw = response.choices[0].message.content.strip().lower()
        route = raw if raw in ROUTE_TOOLS else "general"
        print(f"\n[Router] '{state['message'][:60]}' → specialist: {route.upper()}")
        return {"route": route}

    # ── Route selector (matches course: route_decision) ───────────────────────
    def route_decision(state: WellnessState) -> Literal["nutrition", "fitness", "mental", "metrics", "general"]:
        return state["route"]

    # ── Specialist handlers ──────────────────────────────────────────────────
    def handle_nutrition(state: WellnessState) -> dict:
        return _run_agent("nutrition", state, client, model, openai_tools, tool_registry)

    def handle_fitness(state: WellnessState) -> dict:
        return _run_agent("fitness", state, client, model, openai_tools, tool_registry)

    def handle_mental(state: WellnessState) -> dict:
        return _run_agent("mental", state, client, model, openai_tools, tool_registry)

    def handle_metrics(state: WellnessState) -> dict:
        return _run_agent("metrics", state, client, model, openai_tools, tool_registry)

    def handle_general(state: WellnessState) -> dict:
        return _run_agent("general", state, client, model, openai_tools, tool_registry)

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
