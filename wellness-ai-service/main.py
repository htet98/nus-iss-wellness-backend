import os
from contextlib import asynccontextmanager
from fastapi import FastAPI

from services.rag_service import RAGService
from routers import chat as chat_router
#
#   Author: Htet Nandar
#

rag: RAGService = RAGService()

@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Starting up — loading vector store...")
    rag.build_vector_store()
    chat_router.rag_service = rag          # inject into router
    print("Ready.")
    yield
    print("Shutting down.")


app = FastAPI(title="Wellness AI Service", lifespan=lifespan)

app.include_router(chat_router.router)


@app.get("/api/health")
def health():
    return {"status": "ok", "service": "wellness-ai-service"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=int(os.getenv("PORT", 8000)), reload=True)