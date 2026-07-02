import os
from openai import OpenAI
from services.vector_store import build_or_load_collection
#
#   Author: Htet Nandar
#
CHAT_MODEL = "openai/gpt-4o-mini"

SYSTEM_PROMPT = """You are a wellness assistant. You ONLY answer questions related to health and wellness topics such as nutrition, exercise, sleep, mental health, stress management, and healthy habits.

STRICT RULE: If the user's question is not related to health or wellness, respond ONLY with:
"I'm a wellness assistant and can only help with health and wellness topics. Please ask me about nutrition, exercise, sleep, mental health, or other wellness subjects."

For wellness questions, use the retrieved knowledge below to answer clearly and practically.
If the retrieved knowledge does not cover the question but it is still a wellness topic, give a helpful general wellness answer.
Keep responses concise (3-5 sentences) and friendly.

Retrieved knowledge:
{context}
"""


class RAGService:

    def __init__(self):
        self.collection = None
        self.client = OpenAI(
            base_url="https://openrouter.ai/api/v1",
            api_key=os.getenv("OPENROUTER_API_KEY"),
        )

    def build_vector_store(self):
        self.collection = build_or_load_collection()

    # ── Main chat ──────────────────────────────────────────────────────────

    def chat(self, message: str, history: list, user_context: dict = None) -> str:
        context = self._retrieve(message)

        system_content = SYSTEM_PROMPT.format(context=context)
        if user_context:
            profile_lines = "\n".join(f"  {k}: {v}" for k, v in user_context.items())
            system_content += f"\n\nUser profile:\n{profile_lines}"

        # Build OpenAI-format message list
        messages = [{"role": "system", "content": system_content}]
        for msg in history:
            messages.append({"role": msg.role, "content": msg.content})
        messages.append({"role": "user", "content": message})

        response = self.client.chat.completions.create(
            model=CHAT_MODEL,
            messages=messages,
            temperature=0.7,
        )
        return response.choices[0].message.content

    # ── Semantic retrieval ─────────────────────────────────────────────────

    def _retrieve(self, query: str, k: int = 5) -> str:
        if self.collection is None:
            return ""
        results = self.collection.query(query_texts=[query], n_results=k)
        docs = results.get("documents", [[]])[0]
        return "\n\n".join(docs)
