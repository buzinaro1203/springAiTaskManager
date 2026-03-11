package com.guilherme.todo.todoapi.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.OptimisticLockException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }

  @ExceptionHandler(LLMQuotaExceededException.class)
  public ResponseEntity<Object> handleLLMQuotaExceededException(LLMQuotaExceededException ex) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
  }

  @ExceptionHandler({
      OptimisticLockException.class,
      ObjectOptimisticLockingFailureException.class
  })
  public ResponseEntity<Object> handleOptimisticLockExcepion(Exception ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body("Este recurso foi modificado por outro usuário. Atualize e tente novamente.");
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}