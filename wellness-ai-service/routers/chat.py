from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from services.rag_service import RAGService
#
#   Author: Htet Nandar
#
router = APIRouter(prefix="/api/chat", tags=["chat"])

# Injected by main.py after startup
rag_service: Optional[RAGService] = None


# ── Schemas ────────────────────────────────────────────────────────────────

class Message(BaseModel):
    role: str       # "user" | "assistant"
    content: str

class ChatRequest(BaseModel):
    session_id: Optional[str] = None
    message: str
    history: list[Message] = []
    user_context: Optional[dict] = None

class ChatResponse(BaseModel):
    reply: str
    session_id: Optional[str] = None


# ── Endpoints ──────────────────────────────────────────────────────────────

@router.post("", response_model=ChatResponse)
def send_message(request: ChatRequest):
    if rag_service is None:
        raise HTTPException(status_code=503, detail="Service not ready")
    try:
        reply = rag_service.chat(
            message=request.message,
            history=request.history,
            user_context=request.user_context,
        )
        return ChatResponse(reply=reply, session_id=request.session_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
