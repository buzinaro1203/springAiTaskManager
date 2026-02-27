package com.guilherme.todo.todoapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.guilherme.todo.todoapi.dto.TodoDTO;
import com.guilherme.todo.todoapi.exceptionHandling.ResourceNotFoundException;
import com.guilherme.todo.todoapi.exceptionHandling.UnauthorizedException;
import com.guilherme.todo.todoapi.mapper.TodoMapper;
import com.guilherme.todo.todoapi.model.Category;
import com.guilherme.todo.todoapi.model.Todo;
import com.guilherme.todo.todoapi.model.User;
import com.guilherme.todo.todoapi.repository.CategoryRepository;
import com.guilherme.todo.todoapi.repository.TodoRepository;

@Service
public class TodoService {

  private final TodoRepository todoRepository;
  private final CategoryRepository categoryRepository;
  private final TodoMapper todoMapper;

  public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository, TodoMapper todoMapper) {
    this.todoRepository = todoRepository;
    this.categoryRepository = categoryRepository;
    this.todoMapper = todoMapper;
  }

  public List<TodoDTO> getTodosForUser(User user) {

    return todoRepository.findByUser(user)
        .stream()
        .map(todoMapper::toDTO)
        .collect(Collectors.toList());
  }

  public TodoDTO createTodoForUser(TodoDTO dto, User user) {
    Category category = null;

    Long categoryId = dto.getCategoryId();
    if (categoryId != null) {
      category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }
    Todo todo = todoMapper.toEntity(dto, category, user);
    todo.setCreatedAt(LocalDate.now());
    todo.setUpdatedAt(null);
    Todo savedTodo = todoRepository.save(todo);
    return todoMapper.toDTO(savedTodo);
  }

  public List<TodoDTO> getAllTodos() {
    return todoRepository.findAll()
        .stream()
        .map(todoMapper::toDTO)
        .collect(Collectors.toList());
  }

  // Atualiza um todo de um usuário (valida que pertence ao usuário)
  public TodoDTO updateTodo(Long id, TodoDTO dto, User user) {
    if (id == null) {
      throw new IllegalArgumentException("ID não pode ser nulo");
    }
    return todoRepository.findById(id)
        .map(todo -> {
          if (!todo.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Não autorizado");
          }

          todo.setTitle(dto.getTitle());
          todo.setDescription(dto.getDescription());
          todo.setCompleted(dto.isCompleted());
          todo.setUpdatedAt(LocalDate.now());
          todo.setDueDate(dto.getDueDate());

          if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            todo.setCategory(category);
          }

          todo.setCompletedAt(dto.isCompleted() ? LocalDate.now() : null);
          return todoMapper.toDTO(todoRepository.save(todo));
        }).orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
  }

  // Deleta um todo de um usuário
  public void deleteTodo(Long id, User user) {
    if (id == null) {
      throw new IllegalArgumentException("ID não pode ser nulo");
    }
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
    if (!todo.getUser().getId().equals(user.getId())) {
      throw new UnauthorizedException("Não autorizado");
    }
    todoRepository.deleteById(id);
  }

}