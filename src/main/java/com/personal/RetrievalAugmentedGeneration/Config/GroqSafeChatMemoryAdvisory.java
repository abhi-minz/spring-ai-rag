package com.personal.RetrievalAugmentedGeneration.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

/**
 * A MessageChatMemoryAdvisor that strips `reasoningContent` metadata from
 * assistant messages before replaying them to providers that reject it (e.g.,
 * Groq).
 */
public class GroqSafeChatMemoryAdvisory implements CallAdvisor {

    private final ChatMemory chatMemory;

    public GroqSafeChatMemoryAdvisory(ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        String conversationId = (String) request.context().get(ChatMemory.CONVERSATION_ID);

        List<Message> memoryMessages = chatMemory.get(conversationId);

        List<Message> sanitized = new ArrayList<>();
        for (Message msg : memoryMessages) {
            if (msg instanceof AssistantMessage assistant) {
                Map<String, Object> metadata = new HashMap<>(assistant.getMetadata());
                metadata.remove("reasoningContent");
                metadata.remove("reasoning_content");
                metadata.remove("thinking");

                AssistantMessage clean = AssistantMessage.builder().content(assistant.getText()).properties(metadata)
                        .toolCalls(assistant.getToolCalls()).build();

                sanitized.add(clean);
            } else
                sanitized.add(msg);
        }
        List<Message> allMessages = new ArrayList<>();
        allMessages.add(new UserMessage(request.prompt().getContents()));

        ChatClientRequest sanitizRequest = request.mutate()
                .prompt(request.prompt().mutate().messages(allMessages).build()).build();

        chatMemory.add(conversationId, new UserMessage(request.prompt().getContents()));

        ChatClientResponse response = chain.nextCall(sanitizRequest);

        String assistantText = response.chatResponse().getResult().getOutput().getText();
        chatMemory.add(conversationId, new AssistantMessage(assistantText));

        return response;
    }

    @Override
    public String getName() {
        return "GroqSafeChatMemoryAdvisory";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
