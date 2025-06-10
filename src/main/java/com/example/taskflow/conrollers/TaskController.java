package com.example.taskflow.conrollers;

import com.example.taskflow.dtos.CommentDTO;
import com.example.taskflow.dtos.TaskDTO;
import com.example.taskflow.model.Task;
import com.example.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or @taskService.isTaskCreatedByCurrentUser(#id)")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }


    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Validated @RequestBody TaskDTO taskDTO) {
        return new ResponseEntity<>(taskService.createTask(taskDTO), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or @taskService.isTaskCreatedByCurrentUser(#id)")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Validated @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO));
    }

   //Delete a task.

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isTaskCreatedByCurrentUser(#id)")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    //Get tasks by user.

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or @userService.isCurrentUser(#userId)")
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    //Get tasks by supervisor.

    @GetMapping("/by-supervisor/{supervisorId}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#supervisorId)")
    public ResponseEntity<List<TaskDTO>> getTasksBySupervisor(@PathVariable Long supervisorId) {
        return ResponseEntity.ok(taskService.getTasksBySupervisor(supervisorId));
    }

   //Get tasks by status.

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

   //Get tasks by date.

    @GetMapping("/by-date/{date}")
    public ResponseEntity<List<TaskDTO>> getTasksByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(taskService.getTasksByDate(date));
    }

    //Get tasks by date range.

    @GetMapping("/by-date-range")
    public ResponseEntity<List<TaskDTO>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(taskService.getTasksByDateRange(startDate, endDate));
    }

   //Approve a task.

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<TaskDTO> approveTask(
            @PathVariable Long id,
            @RequestBody(required = false) CommentDTO comment) {
        return ResponseEntity.ok(taskService.approveTask(id, comment));
    }

   //Reject a task.

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<TaskDTO> rejectTask(
            @PathVariable Long id,
            @Valid @RequestBody CommentDTO comment) {
        return ResponseEntity.ok(taskService.rejectTask(id, comment));
    }

   //Get tasks pending validation.

    @GetMapping("/pending-validation")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<List<TaskDTO>> getTasksPendingValidation() {
        return ResponseEntity.ok(taskService.getTasksByStatus(Task.TaskStatus.PENDING));
    }
    
    // Get current user's tasks.

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDTO>> getMyTasks() {
        return ResponseEntity.ok(taskService.getCurrentUserTasks());
    }
}
