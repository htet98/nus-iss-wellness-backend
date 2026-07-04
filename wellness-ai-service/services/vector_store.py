import os
import chromadb
from chromadb.utils.embedding_functions import SentenceTransformerEmbeddingFunction
#
#   Author: Htet Nandar
#
BASE_DIR        = os.path.dirname(os.path.dirname(__file__))
KNOWLEDGE_DIR   = os.path.join(BASE_DIR, "knowledge")
CHROMA_DIR      = os.path.join(BASE_DIR, "chroma_db")
COLLECTION_NAME = "wellness_knowledge"
EMBED_MODEL     = "all-MiniLM-L6-v2"
CHUNK_SIZE      = 800
CHUNK_OVERLAP   = 100


def _chunk_text(text: str) -> list[str]:
    chunks, start = [], 0
    while start < len(text):
        end = start + CHUNK_SIZE
        chunks.append(text[start:end].strip())
        start += CHUNK_SIZE - CHUNK_OVERLAP
    return [c for c in chunks if c]


def build_or_load_collection() -> chromadb.Collection:
    ef     = SentenceTransformerEmbeddingFunction(model_name=EMBED_MODEL)
    client = chromadb.PersistentClient(path=CHROMA_DIR)
    collection = client.get_or_create_collection(
        name=COLLECTION_NAME,
        embedding_function=ef,
    )

    if collection.count() > 0:
        print(f"Loaded existing vector store ({collection.count()} chunks)")
        return collection

    print("Building vector store from knowledge files...")
    documents, ids = [], []
    doc_id = 0

    for filename in sorted(os.listdir(KNOWLEDGE_DIR)):
        if not filename.endswith(".txt"):
            continue
        with open(os.path.join(KNOWLEDGE_DIR, filename), encoding="utf-8") as f:
            text = f.read()
        for chunk in _chunk_text(text):
            documents.append(chunk)
            ids.append(f"chunk_{doc_id}")
            doc_id += 1

    if not documents:
        print("  → No .txt files found in knowledge/ — vector store is empty (LLM will use general knowledge)")
        return collection

    collection.add(documents=documents, ids=ids)
    print(f"  → {len(documents)} chunks added from {KNOWLEDGE_DIR}")
    return collection
