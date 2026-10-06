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

<<<<<<< HEAD
## Running Locally

1. Start Ollama and pull the model:
   ```bash
   ollama pull nomic-embed-text
=======
## Demo

```bash
$ curl "http://localhost:8080/api/search?q=deployment%20failure&k=3"
[
  {"content": "The service will not start after the last deployment.", "metadata": {"source": "doc-1", "distance": 0.317}},
  {"content": "Container keeps restarting in a crash loop.", "metadata": {"source": "doc-3", "distance": 0.490}},
  {"content": "Quarterly revenue grew by 12% in the north region.", "metadata": {"source": "doc-2", "distance": 0.589}}
]
Running Locally
Prerequisites
Java 21

PostgreSQL 16 with the pgvector extension

Ollama installed and running

A Groq API key (free at console.groq.com)

Setup
Pull the embedding model:

bash
ollama pull nomic-embed-text
Set environment variables:

bash
export GROQ_API_KEY="your-groq-key"
export pg_db_password="your-db-password"
Create the database and user (one time):

sql
CREATE DATABASE rag_database;
CREATE USER rag_user WITH PASSWORD 'your-db-password';
GRANT ALL PRIVILEGES ON DATABASE rag_database TO rag_user;
Run the app:

bash
./mvnw spring-boot:run
Architecture
text
┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│   Client    │─────▶│ Spring Boot  │─────▶│    Groq      │
│  (curl/UI)  │      │   (REST)     │      │   (chat)     │
└─────────────┘      └──────┬───────┘      └──────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │   Ollama     │
                     │ (embeddings) │
                     └──────┬───────┘
                            │
                            ▼
                     ┌──────────────┐
                     │  PostgreSQL  │
                     │  + pgvector  │
                     └──────────────┘
>>>>>>> f2f071f (docs: add README with project overview, architecture, and roadmap)
