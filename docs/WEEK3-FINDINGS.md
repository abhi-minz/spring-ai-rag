## Top-K Analysis

Query: "how does multi-head attention work" (21 chunks in DB)

| k | Pages | Distance range | Signal |
|---|-------|---------------|--------|
| 1 | 5 | 0.278 | Top match only |
| 3 | 5, 4, 15 | 0.278–0.344 | All relevant |
| 5 | +13, 14 | 0.278–0.374 | Adds related figures |
| 10 | +1, 2, 3, 6, 7 | 0.278–0.492 | Half are noise |

**Key finding:** A natural distance cutoff exists at ~0.40. Beyond that,
results are topically unrelated to the query.

**Lesson:** Fixed top-k is blunt. Distance-threshold or relative-threshold
retrieval adapts better to query strength.