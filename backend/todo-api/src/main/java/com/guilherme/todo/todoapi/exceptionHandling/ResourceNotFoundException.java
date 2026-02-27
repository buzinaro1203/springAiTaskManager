package com.guilherme.todo.todoapi.exceptionHandling;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }

}
