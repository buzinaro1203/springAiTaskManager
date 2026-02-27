package com.guilherme.todo.todoapi.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

import com.google.genai.errors.ClientException;
import com.guilherme.todo.todoapi.exceptionHandling.LLMQuotaExceededException;

@Service
public class AiService {

	private final ChatClient chatClient;

	// O Starter cria automaticamente o 'builder' configurado com o Gemini
	public AiService(ChatClient.Builder builder) {
		ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();
		MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
				.build();

		this.chatClient = builder
				.defaultAdvisors(memoryAdvisor)
				.defaultToolNames("parseAiSuggestionToTodo", "getTodosPerDate", "completeTodo",
						"deleteTodo",
						"rescheduleTodo")
				.build();

	}

	public String chat(String message) {
		try {
			return chatClient.prompt()
					.system("Você é um assistente que ajuda a criar tarefas para um aplicativo"
							+ " Hoje é " + java.time.LocalDate.now() + "."
							+ " Você possui as ferramentas para criar tarefas, buscar tarefas por data, completar e deletar tarefas. Use-a quando necessário e confirme a criação ao usuário."
							+ " Regras:"
							+ " 1. Para dueDate: Use ESTRITAMENTE o formato YYYY-MM-DD. Se o usuário mencionar horas, ignore-as e use apenas a data referida."
							+ " 2. Para categoryName: Classifique estritamente entre: Pessoal, Trabalho, Estudos ou Outros."
							+ " 3. Para description: Se a tarefa exigir uma lista (ex: compras, ingredientes, passos), NÃO seja sucinto aqui. Liste todos os itens detalhadamente dentro deste campo."
							+ " 4. Para title: Seja curto e direto (verbo + ação).")
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