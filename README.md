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

## Running Locally

1. Start Ollama and pull the model:
   ```bash
   ollama pull nomic-embed-text
