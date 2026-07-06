import os
from contextlib import asynccontextmanager
from fastapi import FastAPI
from dotenv import load_dotenv

load_dotenv()   # loads .env from the working directory before anything else

from services.agent_service import AgentService
from routers import chat as chat_router
#
#   Author: Htet Nandar
#

agent: AgentService = AgentService()

@asynccontextmanager
async def lifespan(_app: FastAPI):
    print("Starting up — loading vector store...")
    agent.build_vector_store()
    chat_router.agent_service = agent      # inject into router
    print("Wellness agent ready.")
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
