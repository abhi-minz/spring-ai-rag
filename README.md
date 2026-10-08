# Spring AI RAG

A Retrieval-Augmented Generation (RAG) chatbot built with Spring Boot, Spring AI, Ollama, and PostgreSQL + pgvector.

## Stack

- **Java 21** + **Spring Boot 4.1**
- **Spring AI 2.0** for LLM integration
- **Groq** for chat (OpenAI-compatible)
- **Ollama** (`nomic-embed-text`) for embeddings
- **PostgreSQL 16** + **pgvector** for vector storage

## Features

- `POST /api/ask` — chat with an LLM (no RAG)
- `POST /api/ingest` — insert a text chunk with metadata
- `POST /api/ingest-document` — chunk and ingest long text
- `GET /api/search?q=...&k=3` — semantic search over stored chunks
- `POST /api/upload-pdf` — upload a PDF and ingest all its chunks
- `GET /api/read-pdf?path=...` — read a PDF's pages without ingesting
- `POST /api/ask-doc` — RAG: answer questions grounded in the ingested documents
- `POST /api/compare` — RAG vs. no-RAG, side-by-side
- `POST /api/chat-doc` — multi-turn RAG with conversation memory

## Demo

```bash
$ curl "http://localhost:8080/api/search?q=deployment%20failure&k=3"
[
  {"content": "The service will not start after the last deployment.", "metadata": {"source": "doc-1", "distance": 0.317}},
  {"content": "Container keeps restarting in a crash loop.", "metadata": {"source": "doc-3", "distance": 0.490}},
  {"content": "Quarterly revenue grew by 12% in the north region.", "metadata": {"source": "doc-2", "distance": 0.589}}
]

## Docs

- [Week 2 Findings](docs/WEEK2-FINDINGS.md)
- [Week 3 Findings](docs/WEEK3-FINDINGS.md)
- [Week 4 Findings](docs/WEEK4-FINDINGS.md)

## Roadmap

- [x] Week 1: Chat endpoint with Groq
- [x] Week 2: Embeddings, pgvector, semantic search, chunking
- [x] Week 3: PDF ingestion (chunking real documents)
- [x] Week 4: Full RAG pipeline (retrieve + generate + memory)
- [ ] Week 5: Frontend + polish
- [ ] Week 6: Evaluation + Docker deployment