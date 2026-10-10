package com.personal.RetrievalAugmentedGeneration.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Wraps Spring AI's {@link ChatClient} to provide simple chat functionality.
 * Uses the configured chat model (Groq via OpenAI-compatible API).
 */
@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Sends a user message to the chat model and returns the response.
     *
     * @param message the user's prompt
     * @return the LLM's reply as plain text
     */
    public String chat(String message) {
        return chatClient.prompt().user(message).call().content();
    }

}
