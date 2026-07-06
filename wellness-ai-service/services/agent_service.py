"""
Wellness AI Service — Agentic entry point.

Flow:
  chat(message, history, user_context)
    │
    ├─ [Guardrails] prompt injection / off-topic / PII
    │
    └─ [LangGraph Workflow]
         router_node → nutrition / fitness / mental / metrics / general
                       └─ agent loop (specialist prompt + filtered tools)
                              └─ reply

Author: Htet Nandar
"""
import os

from services.vector_store import build_or_load_collection
from services import wellness_tools
from services.guardrails import run_input_guardrails


class AgentService:
    """
    Wellness AI agent backed by a LangGraph conditional-routing workflow.
    """

    def __init__(self):
        self.collection = None
        self._workflow  = None   # built after vector store is ready

    def build_vector_store(self):
        self.collection = build_or_load_collection()
        wellness_tools.set_collection(self.collection)

        # Build the conditional-routing workflow (Day 3 pattern).
        # Supports OpenRouter (OPENROUTER_API_KEY) and direct OpenAI (OPENAI_API_KEY).
        from services.workflow import build_wellness_workflow, OPENROUTER_BASE
        openrouter_key = os.getenv("OPENROUTER_API_KEY")
        openai_key     = os.getenv("OPENAI_API_KEY")
        if openrouter_key:
            api_key  = openrouter_key
            base_url = OPENROUTER_BASE
        else:
            api_key  = openai_key   # may still be None if neither is set
            base_url = None         # use OpenAI default endpoint
        self._workflow = build_wellness_workflow(api_key=api_key, base_url=base_url)

    # ── Public entry point ─────────────────────────────────────────────────────

    def chat(
        self,
        message: str,
        history: list,
        user_context: dict,
    ) -> tuple[str, str | None]:

        # ── 1. Input guardrails ────────────────────────────────────────
        refusal, pii_warning = run_input_guardrails(message)
        if refusal:
            return refusal, None

        # ── 2. Conditional routing workflow  ────────────────────────────
        result = self._workflow.invoke({
            "message":      message,
            "history":      history or [],
            "user_context": user_context,
            "route":        "",    # will be set by router_node
            "reply":        "",    # will be set by specialist node
        })

        return result["reply"], pii_warning
