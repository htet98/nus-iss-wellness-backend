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
import sys
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

    async def build_vector_store(self):
        self.collection = build_or_load_collection()
        wellness_tools.set_collection(self.collection)

        # ── Resolve API key / base URL ─────────────────────────────────────
        from services.workflow import build_wellness_workflow, OPENROUTER_BASE
        openrouter_key = os.getenv("OPENROUTER_API_KEY")
        openai_key     = os.getenv("OPENAI_API_KEY")
        if openrouter_key:
            api_key  = openrouter_key
            base_url = OPENROUTER_BASE
            print(f"[AI] Using OpenRouter  key: {openrouter_key[:8]}...{openrouter_key[-4:]}")
        elif openai_key:
            api_key  = openai_key
            base_url = None
            print(f"[AI] Using OpenAI      key: {openai_key[:8]}...{openai_key[-4:]}")
        else:
            api_key  = None
            base_url = None
            print("[AI] WARNING: No API key found — set OPENROUTER_API_KEY or OPENAI_API_KEY in .env")

        # ── Load tools from MCP server (same pattern as course MCP_agent.py) ──
        from langchain_mcp_adapters.client import MultiServerMCPClient
        server_path = os.path.abspath(
            os.path.join(os.path.dirname(__file__), "..", "wellness_mcp_server.py")
        )
        # debug
        print("[DEBUG] python command:", sys.executable)
        print("[DEBUG] server path:", server_path, "| exists:", os.path.exists(server_path))
         
        mcp_client = MultiServerMCPClient({
            "wellness": {
                # "command": "python",
                "command": sys.executable,
                "args": [server_path],
                "transport": "stdio",
            }
        })
        
        # debug
        print("[DEBUG] config:", mcp_client.connections)

        mcp_tools = await mcp_client.get_tools()
        print(f"[AI] MCP tools available: {[t.name for t in mcp_tools]}")

        self._workflow = build_wellness_workflow(
            api_key=api_key, base_url=base_url, mcp_tools=mcp_tools
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
