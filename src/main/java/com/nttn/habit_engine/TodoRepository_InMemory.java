package com.nttn.habit_engine;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TodoRepository_InMemory implements iTodoRepository {
    private final Map<String, Todo> todoStore = new ConcurrentHashMap<>();

    @Override
    public Todo save(Todo todo) {
        if (todo == null) {
            throw new IllegalArgumentException("Todo cannot be null");
        }

        // Update delta before saving to ensure it's current
        todo.updateDelta();

        // If the todo has no ID, generate one
        if (todo.getId() == null || todo.getId().isEmpty()) {
            todo.setId(UUID.randomUUID().toString());
        }

        todoStore.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public Optional<Todo> findById(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(todoStore.get(id));
    }

    @Override
    public List<Todo> findAll() {
        // Return a new list to prevent modification of the internal store
        return new ArrayList<>(todoStore.values());
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            return;
        }
        todoStore.remove(id);
    }

    @Override
    public List<Todo> findByDeadlineBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }

        return todoStore.values().stream()
                .filter(todo -> {
                    LocalDateTime deadline = todo.getDeadline();
                    return (deadline.isEqual(start) || deadline.isAfter(start))
                            && (deadline.isEqual(end) || deadline.isBefore(end));
                })
                .collect(Collectors.toList());
    }

    // Additional helper methods that might be useful

    /**
     * Finds all todos that are overdue (deadline is before current time)
     * 
     * @return List of overdue todos
     */
    public List<Todo> findOverdue() {
        LocalDateTime now = LocalDateTime.now();
        return todoStore.values().stream()
                .filter(todo -> !todo.isCompleted() && todo.getDeadline().isBefore(now))
                .collect(Collectors.toList());
    }

    /**
     * Finds all todos that are not completed and not notified
     * 
     * @return List of todos that need notification
     */
    public List<Todo> findTodosForNotification() {
        return todoStore.values().stream()
                .filter(todo -> !todo.isCompleted() && !todo.isNotified())
                .collect(Collectors.toList());
    }

    /**
     * Finds all completed todos
     * 
     * @return List of completed todos
     */
    public List<Todo> findCompleted() {
        return todoStore.values().stream()
                .filter(Todo::isCompleted)
                .collect(Collectors.toList());
    }
}
