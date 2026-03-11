package com.guilherme.todo.todoapi.ai.memory;

import java.util.Map;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Component;

import com.guilherme.todo.todoapi.model.User;

@Component
public class ChatMemoryManager {
  Map<String, ChatMemory> memoryStore = new java.util.concurrent.ConcurrentHashMap<>();

  private String getConversationID(User user) {
    return "conversation_" + user.getId();
  }

  public ChatMemory getOrCreateMemory(User user) {
    String conversationID = getConversationID(user);
    return memoryStore.computeIfAbsent(
        conversationID, id -> MessageWindowChatMemory.builder().maxMessages(20).build());

  }

  public void clearMemory(User user) {
    String conversationID = getConversationID(user);
    memoryStore.remove(conversationID);

  }

}