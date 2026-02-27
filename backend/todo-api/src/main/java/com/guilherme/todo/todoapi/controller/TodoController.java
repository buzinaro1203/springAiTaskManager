package com.guilherme.todo.todoapi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.guilherme.todo.todoapi.dto.TodoDTO;
import com.guilherme.todo.todoapi.exceptionHandling.ResourceNotFoundException;
import com.guilherme.todo.todoapi.model.User;
import com.guilherme.todo.todoapi.service.TodoService;
import com.guilherme.todo.todoapi.service.UserService;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

  private final TodoService todoService;
  private final UserService userService;

  public TodoController(TodoService todoService, UserService userService) {
    this.todoService = todoService;
    this.userService = userService;
  }

  @GetMapping
  public List<TodoDTO> getTodos(@AuthenticationPrincipal UserDetails userDetails) {
    User user = getAuthenticatedUser(userDetails);
    return todoService.getTodosForUser(user);
  }

  @PostMapping
  public TodoDTO createTodo(@RequestBody TodoDTO todoDTO,
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = getAuthenticatedUser(userDetails);
    return todoService.createTodoForUser(todoDTO, user);
  }

  @PutMapping("/{id}")
  public TodoDTO updateTodo(@PathVariable Long id,
      @RequestBody TodoDTO dto,
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = getAuthenticatedUser(userDetails);
    return todoService.updateTodo(id, dto, user);
  }

  @DeleteMapping("/{id}")
  public void deleteTodo(@PathVariable Long id,
      @AuthenticationPrincipal UserDetails userDetails) {
    User user = getAuthenticatedUser(userDetails);
    todoService.deleteTodo(id, user);
  }

  private User getAuthenticatedUser(UserDetails userDetails) {
    User user = userService.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return user;
  }
}
