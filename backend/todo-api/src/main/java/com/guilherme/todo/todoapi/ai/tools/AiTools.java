package com.guilherme.todo.todoapi.ai.tools;

import com.guilherme.todo.todoapi.model.Category;
import com.guilherme.todo.todoapi.repository.CategoryRepository;
import com.guilherme.todo.todoapi.repository.TodoRepository;
import com.guilherme.todo.todoapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.guilherme.todo.todoapi.dto.AiSuggestion;
import com.guilherme.todo.todoapi.dto.DateQuery;
import com.guilherme.todo.todoapi.dto.IdentifierQuery;
import com.guilherme.todo.todoapi.dto.RescheduleQuery;
import com.guilherme.todo.todoapi.dto.TodoDTO;
import com.guilherme.todo.todoapi.mapper.TodoMapper;
import com.guilherme.todo.todoapi.model.Todo;
import com.guilherme.todo.todoapi.model.User;

@Component
public class AiTools {
  private final CategoryRepository categoryRepository;
  private final TodoMapper todoMapper;
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  private String completeAndSave(Todo todo) {
    todo.setCompleted(true);
    todoRepository.save(todo);
    return "Tarefa '" + todo.getTitle() + "' marcada como concluída.";
  }

  private String delete(Todo todo) {
    String title = todo.getTitle();
    todoRepository.delete(todo);
    return "Tarefa '" + title + "' foi deletada com sucesso.";
  }

  public AiTools(CategoryRepository categoryRepository, TodoMapper todoMapper, UserRepository userRepository,
      TodoRepository todoRepository) {
    this.categoryRepository = categoryRepository;
    this.todoMapper = todoMapper;
    this.userRepository = userRepository;
    this.todoRepository = todoRepository;
  }

  @Transactional
  public TodoDTO parseAiSuggestionToTodo(AiSuggestion suggestion) {
    if (suggestion == null)
      return null;
    List<Category> categories = categoryRepository.findByName(suggestion.categoryName());

    Category category = categories.isEmpty() ? null : categories.get(0);
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).orElseThrow();
    Todo todo = todoMapper.toEntity(suggestion, category, user);
    TodoDTO savedTodo = todoMapper.toDTO(todoRepository.save(todo));
    return savedTodo;
  }

  public List<TodoDTO> getTodosPerDate(DateQuery request) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).orElseThrow();
    java.time.LocalDate startDate = java.time.LocalDate.parse(request.startDate());
    java.time.LocalDate endDate = java.time.LocalDate.parse(request.endDate());
    return todoRepository.findByUserAndDueDateBetween(user, startDate, endDate).stream().map(todoMapper::toDTO)
        .toList();
  }

  @Transactional
  public String completeTodo(IdentifierQuery i) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).orElseThrow();
    try {
      Long id = Long.parseLong(i.identifier().trim());
      Optional<Todo> todoById = todoRepository.findByIdAndUser(id, user);
      if (todoById.isPresent()) {
        Todo todo = todoById.get();
        return completeAndSave(todo);
      } else {
        return "Nenhuma tarefa encontrada com o ID fornecido.";
      }
    } catch (NumberFormatException e) {
    }

    List<Todo> todos = todoRepository.findByUserAndTitleContainingIgnoreCase(user, i.identifier());
    if (todos.isEmpty()) {
      return "Não foi encontrada nenhuma tarefa com esse nome.";
    }
    if (todos.size() == 1) {
      Todo todo = todos.get(0);

      return completeAndSave(todo);
    }
    StringBuilder response = new StringBuilder("Foram encontradas múltiplas tarefas com esse nome:\n");
    for (Todo todo : todos) {
      response.append("- ").append(todo.getTitle()).append(" (ID: ").append(todo.getId()).append(")\n");
    }
    response.append("Por favor, especifique a tarefa que deseja concluir.");
    return response.toString();
  }

  @Transactional
  public String deleteTodo(IdentifierQuery i) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).orElseThrow();
    try {
      Long id = Long.parseLong(i.identifier().trim());
      Optional<Todo> todoById = todoRepository.findByIdAndUser(id, user);
      if (todoById.isPresent()) {
        Todo todo = todoById.get();
        return delete(todo);
      } else {
        return "Nenhuma tarefa encontrada com o ID fornecido.";
      }
    } catch (NumberFormatException e) {
    }

    List<Todo> todos = todoRepository.findByUserAndTitleContainingIgnoreCase(user, i.identifier());
    if (todos.isEmpty()) {
      return "Não foi encontrada nenhuma tarefa com esse nome.";
    }
    if (todos.size() == 1) {
      Todo todo = todos.get(0);

      return delete(todo);
    }
    StringBuilder response = new StringBuilder("Foram encontradas múltiplas tarefas com esse nome:\n");
    for (Todo todo : todos) {
      response.append("- ").append(todo.getTitle()).append(" (ID: ").append(todo.getId()).append(")\n");
    }
    response.append("Por favor, especifique a tarefa que deseja deletar.");
    return response.toString();
  }

  @Transactional
  public String rescheduleTodo(RescheduleQuery query) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByEmail(email).orElseThrow();
    try {
      Long id = Long.parseLong(query.identifier().trim());
      Optional<Todo> todoById = todoRepository.findByIdAndUser(id, user);
      if (todoById.isPresent()) {
        Todo todo = todoById.get();
        java.time.LocalDate dueDate = java.time.LocalDate.parse(query.newDate());
        todo.setDueDate(dueDate);
        todoRepository.save(todo);
        return "Tarefa '" + todo.getTitle() + "' reagendada para " + query.newDate() + ".";
      } else {
        return "Nenhuma tarefa encontrada com o ID fornecido.";
      }
    } catch (NumberFormatException e) {
    }

    List<Todo> todos = todoRepository.findByUserAndTitleContainingIgnoreCase(user, query.identifier());
    if (todos.isEmpty()) {
      return "Não foi encontrada nenhuma tarefa com esse nome.";
    }
    if (todos.size() == 1) {
      Todo todo = todos.get(0);
      java.time.LocalDate dueDate = java.time.LocalDate.parse(query.newDate());
      todo.setDueDate(dueDate);
      todoRepository.save(todo);
      return "Tarefa '" + todo.getTitle() + "' reagendada para " + query.newDate() + ".";
    }
    StringBuilder response = new StringBuilder("Foram encontradas múltiplas tarefas com esse nome:\n");
    for (Todo todo : todos) {
      response.append("- ").append(todo.getTitle()).append(" (ID: ").append(todo.getId()).append(")\n");
    }
    response.append("Por favor, especifique a tarefa que deseja reagendar.");
    return response.toString();
  }
}
