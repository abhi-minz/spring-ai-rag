package com.personal.RetrievalAugmentedGeneration.Service;

public record SourceCard(
        String chunkId,
        String source,
        Object page,
        Object chunkIndex,
        Object totalChunks,
        double distance,
        String text,
        String preview,
        String citationLabel) {
}
