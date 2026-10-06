# Week 2 Findings — Embeddings & Semantic Search

## What Was Built

- Ollama (`nomic-embed-text`) as the local embedding provider (768-dim)
- pgvector extension in PostgreSQL 16 for vector storage
- `POST /api/ingest` — insert a single text chunk
- `POST /api/ingest-document` — chunk and ingest long text
- `GET /api/search?q=...&k=3` — semantic similarity search
- HNSW index on cosine distance for fast retrieval

## Experiments

### Embedding similarity (Day 1)

| Pair | Cosine Similarity |
|------|-------------------|
| "The service will not start" / "Container keeps restarting" | 0.584 |
| "The service will not start" / "Quarterly sales figures" | 0.302 |
| "A dog is playing in the park" / "A puppy is running in the garden" | 0.698 |
| "A dog is playing in the park" / "PostgreSQL is a relational database" | 0.391 |

**Finding:** Semantically related texts score higher even with no shared keywords.

### Chunk size comparison (Day 5)

Tested the same document (~5,000 tokens) at three chunk sizes:

| Chunk Size | Chunks Produced | Avg Chars / Chunk |
|------------|-----------------|-------------------|
| 300 | 10 | 1,398 |
| 800 | 4 | 3,497 |
| 1500 | 2 | 6,995 |

Query: `"what is the first step of rag"`

| Chunk Size | Top-1 Distance | Top-1 Correct? |
|------------|----------------|----------------|
| 800 | 0.425 | ✅ Yes |
| 300 | 0.395 | ❌ No (returned unrelated chunk) |

**Finding:** Smaller chunks produced *lower* cosine distances but retrieved
*semantically incorrect* content. Larger chunks were more reliable for
broad queries. Cosine similarity alone is not a reliable proxy for relevance.

## Lessons Learned

1. **Chunk size is a hyperparameter** — tune it against real queries, not theory.
2. **Cosine distance ≠ relevance** — a low score on the wrong chunk means the
   metric is a proxy, not ground truth.
3. **Metadata matters** — every chunk carries `source`, `chunk_index`, and
   `parent_document_id` for traceability.
4. **Spring AI 2.0 requires explicit provider selection** — use
   `spring.ai.model.chat` and `spring.ai.model.embedding` to avoid bean conflicts.
5. **PostgreSQL 15+ locked down the `public` schema** — `rag_user` needs
   `GRANT CREATE ON SCHEMA public` to create tables.
6. **Ollama `base-url` must be `http://`** — not `https://`. It only speaks
   plain HTTP on `localhost:11434`.

## What's Next (Week 3)

- Ingest a real PDF using Spring AI's `PagePdfDocumentReader`
- Test chunking on real-world content (not just synthetic text)
- Add metadata like page number and file name
- Build a document upload endpoint
