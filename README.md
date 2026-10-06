# Spring AI RAG

A Retrieval-Augmented Generation (RAG) chatbot built with Spring Boot, Spring AI, Ollama, and PostgreSQL + pgvector.

## Stack

- **Java 21** + **Spring Boot 4.1**
- **Spring AI 2.0** for LLM integration
- **Groq** for chat (OpenAI-compatible)
- **Ollama** (`nomic-embed-text`) for embeddings
- **PostgreSQL 16** + **pgvector** for vector storage

## Features

- `POST /api/ask` — chat with an LLM
- `POST /api/ingest` — insert a text chunk with metadata
- `GET /api/search?q=...&k=3` — semantic search over stored chunks

## Demo

```bash
$ curl "http://localhost:8080/api/search?q=deployment%20failure&k=3"
[
  {"content": "The service will not start after the last deployment.", "metadata": {"source": "doc-1", "distance": 0.317}},
  {"content": "Container keeps restarting in a crash loop.", "metadata": {"source": "doc-3", "distance": 0.490}},
  {"content": "Quarterly revenue grew by 12% in the north region.", "metadata": {"source": "doc-2", "distance": 0.589}}
]

## Roadmap

- [x] Week 1: Chat endpoint with Groq
- [x] Week 2: Embeddings, pgvector, semantic search, chunking
- [ ] Week 3: Document ingestion (PDF chunking)
- [ ] Week 4: Full RAG pipeline (retrieve + generate)
- [ ] Week 5: Frontend + chat memory
- [ ] Week 6: Evaluation + Docker deployment