package com.guilherme.todo.todoapi.exceptionHandling;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}