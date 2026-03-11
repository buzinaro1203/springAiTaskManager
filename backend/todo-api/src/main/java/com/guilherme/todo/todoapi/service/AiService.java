package com.guilherme.todo.todoapi.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.genai.errors.ClientException;
import com.guilherme.todo.todoapi.ai.memory.ChatMemoryManager;
import com.guilherme.todo.todoapi.exceptionHandling.LLMQuotaExceededException;
import com.guilherme.todo.todoapi.model.User;
import com.guilherme.todo.todoapi.repository.UserRepository;

@Service
public class AiService {

	private final ChatClient chatClient;
	private final ChatMemoryManager chatMemoryManager;
	private final UserRepository userRepository;

	public AiService(ChatClient.Builder builder, ChatMemoryManager chatMemoryManager, UserRepository userRepository) {
		this.chatMemoryManager = chatMemoryManager;
		this.userRepository = userRepository;
		this.chatClient = builder
				.defaultToolNames("parseAiSuggestionToTodo", "getTodosPerDate", "completeTodo",
						"deleteTodo",
						"rescheduleTodo")
				.build();

	}

	public void resetChat() {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = userRepository.findByEmail(email).orElseThrow();

		chatMemoryManager.clearMemory(user);
	}

	public String chat(String message) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email).orElseThrow();
		ChatMemory chatMemory = chatMemoryManager.getOrCreateMemory(user);
		MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
				.build();

		try {
			return chatClient.prompt()
					.system("""
							Você é um assistente que ajuda a criar tarefas para um aplicativo.

							Hoje é %s.

							Você possui ferramentas para:
							- criar tarefas
							- buscar tarefas por data
							- completar tarefas
							- remarcar prazo
							- deletar tarefas

							Use-as quando necessário e confirme ao usuário.

							Regras:
							1. dueDate: usar formato YYYY-MM-DD
							2. ignore horários
							3. categoryName: Pessoal, Trabalho, Estudos ou Outros
							4. description: detalhar listas completas
							5. title: curto e direto (verbo + ação)
							""".formatted(java.time.LocalDate.now()))
					.advisors(memoryAdvisor)
					.user(message)
					.call().content();
		} catch (ClientException e) {
			if (e.code() == 429) {
				throw new LLMQuotaExceededException("Limite de uso da IA atingido. Por favor, tente novamente mais tarde.");

			} else {
				throw e; // relança outras exceções
			}
		}

	}

}