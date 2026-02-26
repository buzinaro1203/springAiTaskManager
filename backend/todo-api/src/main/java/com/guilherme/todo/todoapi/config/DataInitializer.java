package com.guilherme.todo.todoapi.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.guilherme.todo.todoapi.model.Category;
import com.guilherme.todo.todoapi.repository.CategoryRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCategories(CategoryRepository repo) {
        return args -> {

            createIfNotExists("Pessoal", repo);
            createIfNotExists("Trabalho", repo);
            createIfNotExists("Estudos", repo);
            createIfNotExists("Outros", repo);
        };
    }

    private void createIfNotExists(String name, CategoryRepository repo) {
        if (!repo.existsByName(name)) {
            repo.save(new Category(name));
        }
    }
}