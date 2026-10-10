package com.personal.RetrievalAugmentedGeneration.Service;

import org.springframework.stereotype.Service;

/**
 * Normalizes text extracted from PDFs to remove artifacts
 * (excessive whitespace, form feeds, blank lines) that hurt
 * embedding quality.
 */
@Service
public class TextCleanupService {

    public String clean(String raw) {
        if (raw == null)
            return "";
        return raw.replaceAll("[\\t\\x08\\f\\r]+", " ").replaceAll(" {2,}", " ").replaceAll("(?m)^\\\\s+", " ")
                .replaceAll("\\n{3,}", "\n\n").trim();
    }
}