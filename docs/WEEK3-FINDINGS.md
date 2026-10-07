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
## Distance Threshold Experiment

Instead of fixed top-k, filter by relative distance (best + threshold).

| Threshold | Chunks kept | Pages |
|-----------|-------------|-------|
| best + 0.03 | 2 | 5, 4 |
| best + 0.05 | 3 | 5, 4, 15 |
| best + 0.10 | 5 | 5, 4, 15, 14, 13 |
| best + 0.20 | ~8 | + 1, 7, 3 |

**Trade-off:** Higher threshold = more recall, lower precision.
**Chosen default:** best + 0.05 (returns 3 chunks — matches k=3 quality).

**Lesson:** Query strength varies. Fixed top-k is blunt. Distance thresholds
adapt to the query but require tuning.