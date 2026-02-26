package com.guilherme.todo.todoapi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "todos")
public class Todo {
  // Fields
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String title;

  @Column(length = 255)
  private String description;

  @Column(nullable = false, updatable = true)
  private boolean completed = false;

  @Column(name = "due_date", nullable = false, updatable = true)
  private LocalDate dueDate;

  @Column(nullable = false, updatable = false)
  private LocalDate createdAt;

  @Column(nullable = true)
  private LocalDate updatedAt;

  @Column(nullable = true)
  private LocalDate completedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  // Aqui entra a relação: muitos Todos pertencem a uma Category
  @ManyToOne
  @JoinColumn(name = "category_id") // nome da FK na tabela todos
  private Category category;

  // Default constructor
  public Todo() {
  }

  // On create and update methods
  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDate.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDate.now();
    if (this.completed && this.completedAt == null) {
      this.completedAt = LocalDate.now();
    }
    if (!this.completed && this.completedAt != null) { // Reset completedAt if not completed
      this.completedAt = null;
    }
  }

  // Getters and Setters
  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDate getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDate updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDate getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(LocalDate completedAt) {
    this.completedAt = completedAt;
  }

}
