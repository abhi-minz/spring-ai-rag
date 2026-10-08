package com.personal.RetrievalAugmentedGeneration.Service;

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

        public String ask(String sessionId, String question, int topK) {
                var chunks = vectorStoreService.search(question, topK);
                var relevant = chunks.stream().filter(d -> {
                        Object dist = d.getMetadata().get("distance");
                        return dist != null && ((Number) dist).doubleValue() < 0.55;
                }).toList();

                String context = relevant.isEmpty() ? "No relevant documents found."
                                : relevant
                                                .stream().map(d -> String.format("[page %s] %s",
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

                return chatClient.prompt().user(prompt).advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                                .call()
                                .content();

        }

        public void clearSession(String sessionId) {

        }

}