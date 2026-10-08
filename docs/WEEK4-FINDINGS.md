# Week 4 Findings — Full RAG Loop

## What Was Built

| Endpoint | Purpose |
|----------|---------|
| `POST /api/ask-doc` | Single-turn RAG with citations |
| `POST /api/compare` | Side-by-side RAG vs. no-RAG |
| `POST /api/chat-doc` | Multi-turn RAG with conversation memory |

**New services:**
- `RagService` — retrieve → filter → generate
- `ChatWithMemoryService` — multi-turn RAG
- `GroqSafeChatMemoryAdvisor` — custom advisor for Groq compatibility

## The RAG Loop

User question
    ↓

Embed question (Ollama, nomic-embed-text, 768-dim)
    ↓

Search pgvector, top-k by cosine distance
    ↓

Filter: keep chunks with distance < 0.55
    ↓

4a. If empty → return "I don't know" (no LLM call)
4b. Else → build prompt with [page N] citations
    ↓

Call Groq LLM (gpt-oss-120b)
    ↓

Return answer + source cards

## RAG vs. No-RAG Comparison

### Case 1: Technical question
**Q:** "How does multi-head attention work?"

| | Without RAG | With RAG |
|---|-------------|----------|
| Length | 2,000+ words | 5 bullet points |
| Content | Generic, pseudocode, variants | Exact math + config from paper |
| Citations | None | `[page 4]`, `[page 5]` |
| Specifics | "h commonly 8 or 12" | "h=8, dk=dv=64, dmodel=512" |

### Case 2: Attribution
**Q:** "Who proposed scaled dot-product attention?"

- **Without RAG:** Lists all 8 authors, misses the specific attribution
- **With RAG:** "Noam Shazeer [page 1]" — pulled from footnote

### Case 3: Contradiction detection
**Q:** "What BLEU score did the big Transformer achieve on English-to-French?"

- **Without RAG:** States 41.0 as fact
- **With RAG:** Reports both 41.0 (page 8) and 41.8 (page 1, abstract), flags the inconsistency

**Key finding:** RAG doesn't just answer questions — it surfaces ground truth and contradictions.

## Multi-Turn Chat with Memory

### The Problem

Spring AI's `MessageChatMemoryAdvisor` is a **final class** — cannot be subclassed.
It also preserves `reasoningContent` metadata from assistant messages, which
**Groq's API rejects** with a 400 error when replaying history.

### The Solution

Custom `GroqSafeChatMemoryAdvisor`:
- Implements Spring AI's `CallAdvisor` interface directly
- Strips `reasoningContent`, `reasoning_content`, and `thinking` from assistant messages
- Uses `MessageWindowChatMemory` (20-message window) with `InMemoryChatMemoryRepository`

### Verified Behavior

| Test | Result |
|------|--------|
| Session isolation | Different sessionIds have independent history ✅ |
| Follow-up context | "more heads" → resolves to "multi-head attention" ✅ |
| Memory-dependent questions | "the model we discussed" → answered from history ✅ |
| Citations preserved across turns | ✅ |

## Lessons Learned

1. **Distance threshold short-circuit** saves ~99% latency on off-topic queries (6s → 50ms, zero tokens)
2. **Prompt rules shape output** — citation rule → every claim cited
3. **Framework incompatibilities are real** — Spring AI + Groq required a custom advisor
4. **Custom advisors are the escape hatch** — when built-in advisors are too rigid, implement `CallAdvisor`
5. **RAG's biggest value is verifiability** — not just better answers, but answers you can trace
6. **Meta-questions are a known limitation** — "what did we discuss?" fails when retrieval has no hits

## What's Next (Week 5)

- Streamlit or React frontend
- Chat UI with source citations rendered as clickable cards
- File upload interface for PDFs
- Optional: session picker, history

## Known Limitations

- Memory is in-process — restarts clear all sessions
- Meta-questions ("what did we discuss") can't be answered when retrieval returns no hits
- No re-ranking — retrieval relies solely on cosine distance
- No hybrid search (keyword + vector)