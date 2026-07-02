"""
Agentic Wellness AI Service.

Architecture (NUS ISS Single Agent Deep Dive pattern):
  ┌─────────────────────────────────────────────────────┐
  │  Input Guardrails → Agent Loop → Output             │
  │                                                     │
  │  Agent Loop:                                        │
  │    1. Build messages (system + history + user msg)  │
  │    2. Call LLM with tool schemas                    │
  │    3. LLM returns tool_call(s) → execute tools      │
  │    4. Append tool results → go to step 2            │
  │    5. LLM returns text (no tool_call) → done        │
  └─────────────────────────────────────────────────────┘

Memory:
  - In-context: full conversation history passed per request (managed by Spring Boot)
  - External:   ChromaDB knowledge base, queried via search_wellness_knowledge tool

Tools (agent decides which to call):
  - search_wellness_knowledge  → RAG over ChromaDB
  - calculate_bmi              → BMI + weight category
  - calculate_daily_calories   → TDEE using Mifflin-St Jeor

Guardrails:
  - Prompt injection detection (hard block)
  - Off-topic detection (hard block)
  - PII detection (soft warning prepended to response)
  - Budget guardrail: max 5 LLM calls per request

Author: Htet Nandar
"""
import json
import os
from openai import OpenAI
from openai.types.chat import ChatCompletionMessage

from services.vector_store import build_or_load_collection
from services import wellness_tools
from services.wellness_tools import TOOLS, TOOL_REGISTRY
from services.guardrails import run_input_guardrails

# ── Constants ──────────────────────────────────────────────────────────────

CHAT_MODEL     = "openai/gpt-4o-mini"
MAX_ITERATIONS = 5  # budget guardrail: max tool-call rounds per request

SYSTEM_PROMPT = """You are a friendly, knowledgeable wellness assistant.

You help users with:
- Nutrition and healthy eating
- Exercise and fitness
- Sleep hygiene
- Mental health and stress management
- General healthy lifestyle habits

You have access to tools:
1. search_wellness_knowledge — search an evidence-based wellness knowledge base
2. calculate_bmi             — compute BMI and weight category from weight/height
3. calculate_daily_calories  — estimate daily calorie needs (TDEE)

GUIDELINES:
- Always use search_wellness_knowledge when answering factual wellness questions.
- Use calculate_bmi or calculate_daily_calories when the user provides measurements.
- You may call multiple tools in a single turn if needed.
- Keep final answers concise (3–5 sentences) and friendly.
- If a question is outside wellness topics, politely decline.
- If the knowledge base has no relevant info, give a sound general wellness answer.
"""


class AgentService:
    """
    Wellness AI agent with tool-calling loop.
    Replaces the simple RAGService single-call pattern.
    """

    def __init__(self):
        self.collection = None
        self.client = OpenAI(
            base_url="https://openrouter.ai/api/v1",
            api_key=os.getenv("OPENROUTER_API_KEY"),
        )

    def build_vector_store(self):
        """Load (or build) the ChromaDB collection and inject into tools."""
        self.collection = build_or_load_collection()
        wellness_tools.set_collection(self.collection)

    # ── Public entry point ─────────────────────────────────────────────────

    def chat(
        self,
        message: str,
        history: list,
        user_context: dict = None,
    ) -> tuple[str, str | None]:
        """
        Process a user message through the agent loop.

        Args:
            message:      The user's latest message.
            history:      List of previous Message objects (role + content).
            user_context: Optional dict of user profile info (age, weight, etc.).

        Returns:
            (reply, pii_warning) — pii_warning is None if no PII detected.
        """
        # ── 1. Input guardrails ────────────────────────────────────────────
        refusal, pii_warning = run_input_guardrails(message)
        if refusal:
            return refusal, None

        # ── 2. Build message list ──────────────────────────────────────────
        system_content = SYSTEM_PROMPT
        if user_context:
            profile = "\n".join(f"  {k}: {v}" for k, v in user_context.items())
            system_content += f"\n\nUser profile (use when relevant):\n{profile}"

        messages = [{"role": "system", "content": system_content}]

        # In-context memory: full session history from Spring Boot
        for msg in history:
            messages.append({"role": msg.role, "content": msg.content})

        messages.append({"role": "user", "content": message})

        # ── 3. Agent loop (budget guardrail: max MAX_ITERATIONS rounds) ────
        for iteration in range(MAX_ITERATIONS):
            response = self.client.chat.completions.create(
                model=CHAT_MODEL,
                messages=messages,
                tools=TOOLS,
                tool_choice="auto",
                temperature=0.7,
            )

            assistant_msg: ChatCompletionMessage = response.choices[0].message

            # No tool calls → LLM produced a final text answer
            if not assistant_msg.tool_calls:
                return assistant_msg.content, pii_warning

            # Append the assistant's tool-call decision to the conversation
            messages.append(assistant_msg)

            # Execute each tool the LLM requested
            for tool_call in assistant_msg.tool_calls:
                tool_result = self._execute_tool(
                    name=tool_call.function.name,
                    args_json=tool_call.function.arguments,
                )
                # Feed the result back as a tool message
                messages.append({
                    "role":         "tool",
                    "tool_call_id": tool_call.id,
                    "content":      tool_result,
                })

        # ── 4. Budget exhausted — force a final text answer ────────────────
        messages.append({
            "role":    "user",
            "content": "Please give me your final answer based on what you've found so far.",
        })
        fallback = self.client.chat.completions.create(
            model=CHAT_MODEL,
            messages=messages,
            temperature=0.7,
        )
        return fallback.choices[0].message.content, pii_warning

    # ── Private helpers ────────────────────────────────────────────────────

    def _execute_tool(self, name: str, args_json: str) -> str:
        """Execute a tool by name and return its result as a string."""
        if name not in TOOL_REGISTRY:
            return json.dumps({"error": f"Unknown tool: {name}"})
        try:
            args = json.loads(args_json)
            result = TOOL_REGISTRY[name](**args)
            return result if isinstance(result, str) else json.dumps(result)
        except Exception as e:
            return json.dumps({"error": str(e)})
