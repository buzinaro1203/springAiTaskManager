package com.guilherme.todo.todoapi.exceptionHandling;

public class LLMQuotaExceededException extends RuntimeException {
  public LLMQuotaExceededException(String message) {
    super(message);
  }
  
}
