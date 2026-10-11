package com.personal.RetrievalAugmentedGeneration.Service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import com.personal.RetrievalAugmentedGeneration.Config.GroqSafeChatMemoryAdvisory;

/**
 * Multi-turn RAG chat service. Maintains conversation history per session
 * so follow-up questions can reference earlier context.
 */
@Service
public class ChatWithMemoryService {

        private final ChatClient chatClient;
        private final VectorStoreService vectorStoreService;

        public ChatWithMemoryService(ChatClient.Builder chatClientBuilder, VectorStoreService vectorStoreService,
                        ChatMemory chatMemory) {
                this.chatClient = chatClientBuilder.defaultAdvisors(new GroqSafeChatMemoryAdvisory(chatMemory)).build();
                this.vectorStoreService = vectorStoreService;
        }

        public Map<String, Object> ask(String sessionId, String question, int topK) {
                var chunks = vectorStoreService.search(question, topK);
                var relevant = chunks.stream().filter(d -> {
                        Object dist = d.getMetadata().get("distance");
                        return dist != null && ((Number) dist).doubleValue() < 0.55;
                }).toList();

                if (relevant.isEmpty()) {
                        return Map.of("sessionId", sessionId, "question", question, "answer",
                                        "I don't know based on the provided documents.", "sources", List.of(), "topK",
                                        topK, "filtered", chunks.size());
                }

                String context = relevant.stream().map(d -> String.format("[page %s] %s",
                                d.getMetadata().getOrDefault("page_number", "?"),
                                d.getText()))
                                .reduce("", (a, b) -> a + "\n\n---\n\n" + b);

                String prompt = """
                                You are a research assistant. Answer using ONLY the context below.
                                Cite every factual claim with [page N].
                                If the context doesn't contain the answer, say:
                                "I don't know based on the provided documents."

                                Context:
                                %s

                                Question: %s
                                """.formatted(context, question);

                String answer = chatClient.prompt().user(prompt)
                                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                                .call()
                                .content();

                List<Map<String, Object>> sources = relevant.stream().map(d -> {
                        Object page = d.getMetadata().getOrDefault("page_number", "?");
                        Object source = d.getMetadata().getOrDefault("source", "unknown");
                        Object chunkIdx = d.getMetadata().getOrDefault("chunk_index", "?");
                        Object totalChunks = d.getMetadata().getOrDefault("total_chunks", "?");
                        Object rawDist = d.getMetadata().get("distance");
                        double dist = rawDist == null ? -1.0 : ((Number) rawDist).doubleValue();
                        String text = d.getText();
                        String preview = text.substring(0, Math.min(150, text.length()));

                        return Map.<String, Object>of("chunkId", d.getId(), "source", source.toString(), "page", page,
                                        "chunkIndex", chunkIdx, "totalChunks", totalChunks, "distance", dist, "text",
                                        text, "preview", preview, "citationLabel",
                                        String.format("[%s, page %s]", source, page));
                }).toList();

                return Map.of(
                                "sessionId", sessionId,
                                "question", question,
                                "answer", answer,
                                "sources", sources,
                                "topK", topK,
                                "filtered", chunks.size() - relevant.size());
        }

        public void clearSession(String sessionId) {

        }

}