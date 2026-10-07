package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

/**
 * Retrieval-Augmented Generation service.
 * Retrieves relevant chunks from the vector store, builds a grounded
 * prompt, and calls the LLM to produce an answer.
 */
@Service
public class RagService {

    private static final double MAX_DISTANCE = 0.55;

    private final VectorStoreService vectorStoreService;
    private final ChatClient chatClient;

    public RagService(VectorStoreService vectorStoreService, ChatClient.Builder chatClientBuilder) {
        this.vectorStoreService = vectorStoreService;
        this.chatClient = chatClientBuilder.build();
    }

    public Map<String, Object> ask(String question, int topK) {

        List<Document> candidates = vectorStoreService.search(question, topK);

        List<Document> relevant = candidates.stream().filter(d -> {
            Object dist = d.getMetadata().get("distance");
            return dist != null && ((Number) dist).doubleValue() < MAX_DISTANCE;
        }).toList();

        if (relevant.isEmpty()) {
            return Map.of("question", question, "answer", "I don't know based on the provided documents.", "sources",
                    List.of(), "topK", topK, "filtered", candidates.size());
        }

        String context = relevant.stream()
                .map(d -> String.format("[page %s] %s", d.getMetadata().getOrDefault("page_number", "?"), d.getText()))
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = """
                You are an assistant answering questions based on the provided context.
                Answer ONLY using the context below. If the context does not contain
                the answer, respond with: "I don't know based on the provided documents."

                Context:
                %s

                Question: %s

                Answer:
                """.formatted(context, question);

        String answer = chatClient.prompt().user(prompt).call().content();

        List<Map<String, Object>> sources = relevant.stream()
                .map(d -> Map.<String, Object>of("page", d.getMetadata().getOrDefault("page_number", "?"), "distance",
                        d.getMetadata().getOrDefault("distance", -1), "preview",
                        d.getText().substring(0, Math.min(150, d.getText().length()))))
                .toList();

        return Map.of("question", question, "answer", answer, "sources", sources, "topK", topK);
    }

}
