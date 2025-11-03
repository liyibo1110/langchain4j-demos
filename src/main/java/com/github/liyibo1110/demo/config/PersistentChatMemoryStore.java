package com.github.liyibo1110.demo.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.List;

/**
 * @author liyibo
 * @date 2025-11-03 14:30
 */
public class PersistentChatMemoryStore implements ChatMemoryStore {
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return null;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {

    }

    @Override
    public void deleteMessages(Object memoryId) {

    }
}
