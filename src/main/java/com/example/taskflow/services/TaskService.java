package com.example.taskflow.services;

import com.example.taskflow.model.Task;
import com.example.taskflow.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // Create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Get a task by ID
    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Update an existing task
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    // Delete a task by ID
    public void deleteTaskById(Integer id) {
        taskRepository.deleteById(id);
    }
}
