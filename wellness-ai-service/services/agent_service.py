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

        # Build the conditional-routing workflow (Day 3 pattern)
        from services.workflow import build_wellness_workflow
        self._workflow = build_wellness_workflow(
            api_key=os.getenv("OPENROUTER_API_KEY")
        )

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
