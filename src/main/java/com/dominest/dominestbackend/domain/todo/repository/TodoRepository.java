package com.dominest.dominestbackend.domain.todo.repository;

import com.dominest.dominestbackend.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}