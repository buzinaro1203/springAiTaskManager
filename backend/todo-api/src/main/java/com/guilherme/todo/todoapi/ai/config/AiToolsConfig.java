package com.guilherme.todo.todoapi.ai.config;

import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import com.guilherme.todo.todoapi.ai.tools.AiTools;
import com.guilherme.todo.todoapi.dto.AiSuggestion;
import com.guilherme.todo.todoapi.dto.DateQuery;
import com.guilherme.todo.todoapi.dto.IdentifierQuery;
import com.guilherme.todo.todoapi.dto.RescheduleQuery;
import com.guilherme.todo.todoapi.dto.TodoDTO;

@Configuration
public class AiToolsConfig {
  AiTools aiTools;

  public AiToolsConfig(AiTools aiTools) {
    this.aiTools = aiTools;
  }

  @Description("A funçao serve para criar uma tarefa a partir de uma sugestao da IA e ja injetando no banco de dados e retorna um TodoDTO")
  @Bean
  public Function<AiSuggestion, TodoDTO> parseAiSuggestionToTodo() {
    return aiTools::parseAiSuggestionToTodo;
  }

  @Description("A funçao serve para buscar todas as tarefas de um usuario por data de vencimento")
  @Bean
  public Function<DateQuery, List<TodoDTO>> getTodosPerDate() {
    return aiTools::getTodosPerDate;
  }

  @Description("Marca uma tarefa como concluída. Aceita o NOME da tarefa ou o ID numérico. Se houver ambiguidade, retorna lista de IDs.")
  @Bean
  public Function<IdentifierQuery, String> completeTodo() {
    return aiTools::completeTodo;
  }

  @Description("Exclui uma tarefa permanentemente. Aceita Nome ou ID.")
  @Bean
  public Function<IdentifierQuery, String> deleteTodo() {
    return aiTools::deleteTodo;
  }

  @Description("Reagenda uma tarefa para uma nova data. Exige o identificador (Nome ou ID) e a nova data (YYYY-MM-DD).")
  @Bean
  public Function<RescheduleQuery, String> rescheduleTodo() {
    return aiTools::rescheduleTodo;
  }

}
