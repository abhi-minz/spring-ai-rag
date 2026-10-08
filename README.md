# Spring AI RAG

A production-grade Retrieval-Augmented Generation (RAG) chatbot built with Spring Boot, Spring AI, Ollama, PostgreSQL + pgvector, and React.

## Architecture
React Frontend (:5173)
↓ HTTP
Spring Boot Backend (:8080)
↓
Groq (chat) + Ollama (embeddings) + PostgreSQL/pgvector

text

## Structure

- `backend/` — Spring Boot + Spring AI + Java 21
- `frontend/` — React + Vite SPA
- `docs/` — findings, design notes
- `data/` — test PDFs, Jupyter notebooks

## Stack

- **Java 21** + **Spring Boot 4.1**
- **Spring AI 2.0** for LLM integration
- **Groq** for chat (OpenAI-compatible)
- **Ollama** (`nomic-embed-text`) for embeddings
- **PostgreSQL 16** + **pgvector** for vector storage
- **React + Vite** for the frontend
- **Docker** (Week 6) for deployment

## Features

### Backend Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/ask` | Chat with an LLM (no RAG) |
| POST | `/api/ingest` | Insert a text chunk with metadata |
| POST | `/api/ingest-document` | Chunk and ingest long text |
| GET | `/api/search?q=...&k=3` | Semantic search over stored chunks |
| POST | `/api/upload-pdf` | Upload a PDF and ingest all its chunks |
| GET | `/api/read-pdf?path=...` | Read a PDF's pages without ingesting |
| POST | `/api/ask-doc` | RAG: answer questions grounded in the ingested documents |
| POST | `/api/compare` | RAG vs. no-RAG, side-by-side |
| POST | `/api/chat-doc` | Multi-turn RAG with conversation memory |

### Frontend

- Chat interface calling the RAG backend
- Source cards showing page numbers and distances
- PDF upload UI
- Session management for multi-turn conversations

## Quick Start

### Backend

```bash
cd backend
./mvnw spring-boot:run
Requires:

Ollama running (ollama serve) with nomic-embed-text pulled

PostgreSQL running with pgvector extension enabled

Environment variables: GROQ_API_KEY, pg_db_password

Frontend
bash
cd frontend
npm install
npm run dev
Opens at http://localhost:5173

Demo
bash
curl "http://localhost:8080/api/search?q=multi-head%20attention&k=3"
json
[
  {"content": "Multi-head attention allows the model to jointly attend to information...", "metadata": {"page": 5, "distance": 0.278}},
  {"content": "Scaled Dot-Product Attention Multi-Head Attention...", "metadata": {"page": 4, "distance": 0.311}},
  {"content": "Figure 5: Many of the attention heads exhibit behaviour...", "metadata": {"page": 15, "distance": 0.346}}
]
RAG in action
bash
curl -X POST http://localhost:8080/api/ask-doc \
  -H "Content-Type: application/json" \
  -d '{"question": "How does multi-head attention work?", "topK": 3}'
Returns an answer with [page N] citations and structured source metadata.

Docs
Week 2 Findings — Embeddings & Semantic Search

Week 3 Findings — PDF Ingestion

Week 4 Findings — Full RAG Loop

Roadmap
☑ Week 1: Chat endpoint with Groq
☑ Week 2: Embeddings, pgvector, semantic search, chunking
☑ Week 3: PDF ingestion (chunking real documents)
☑ Week 4: Full RAG pipeline (retrieve + generate + memory)
□ Week 5: React frontend + polish
□ Week 6: Evaluation + Docker deployment