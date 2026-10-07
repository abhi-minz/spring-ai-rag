# Week 4 Findings — RAG Loop

## What Was Built

- `RagService.ask()` — retrieve → filter → generate
- `RagService.compare()` — side-by-side RAG vs. no-RAG
- `POST /api/ask-doc` — grounded Q&A with citations
- `POST /api/compare` — proof that RAG matters
- Distance-threshold short-circuit (saves LLM calls for off-topic queries)
- Structured `SourceCard` response type

## The RAG Loop
