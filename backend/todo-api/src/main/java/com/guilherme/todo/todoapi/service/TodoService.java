package com.guilherme.todo.todoapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public TodoDTO createTodoForUser(TodoDTO dto, User user) {
    Category category = null;

    Long categoryId = dto.getCategoryId();
    if (categoryId != null) {
      category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }
    Todo todo = todoMapper.toEntity(dto, category, user);
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
  @Transactional
  public TodoDTO updateTodo(Long id, TodoDTO dto, User user) {

    Todo todo = todoRepository.findByIdAndUser(id, user)
        .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

    todo.setTitle(dto.getTitle());
    todo.setDescription(dto.getDescription());
    todo.setCompleted(dto.isCompleted());
    todo.setDueDate(dto.getDueDate());
    todo.setCompletedAt(dto.isCompleted() ? LocalDate.now() : null);
    return todoMapper.toDTO(todo);
  }

  // Deleta um todo de um usuário
  @Transactional
  public void deleteTodo(Long id, User user) {
    if (id == null) {
      throw new IllegalArgumentException("ID não pode ser nulo");
    }
    Todo todo = todoRepository.findByIdAndUser(id, user)
        .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));
    if (!todo.getUser().getId().equals(user.getId())) {
      throw new UnauthorizedException("Não autorizado");
    }
    todoRepository.deleteById(id);
  }

}