package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.CommentDTO;
import com.example.taskflow.dtos.TaskDTO;
import com.example.taskflow.dtos.TaskStatisticsDTO;
import com.example.taskflow.exceptions.ResourceNotFoundException;
import com.example.taskflow.exceptions.UnauthorizedException;
import com.example.taskflow.model.Comment;
import com.example.taskflow.model.Task;
import com.example.taskflow.model.User;
import com.example.taskflow.repositories.CommentRepository;
import com.example.taskflow.repositories.TaskRepository;
import com.example.taskflow.repositories.UserRepository;
import com.example.taskflow.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return convertToDTO(task);
    }

    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        User currentUser = getCurrentUserEntity();
        
        Task task = convertToEntity(taskDTO);
        task.setUser(currentUser);
        task.setStatus(Task.TaskStatus.PENDING);
        
        // If current user is an admin or supervisor, they can create tasks for other users
        if (taskDTO.getUserId() != null && (hasRole("ROLE_ADMIN") || hasRole("ROLE_SUPERVISOR"))) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
            task.setUser(user);
        }
        
        // Set supervisor
        if (taskDTO.getSupervisorId() != null) {
            User supervisor = userRepository.findById(taskDTO.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found with id: " + taskDTO.getSupervisorId()));
            task.setSupervisor(supervisor);
        }
        
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Only task creator, admin, or supervisor can update the task
        User currentUser = getCurrentUserEntity();
        if (!task.getUser().getId().equals(currentUser.getId()) && 
                !hasRole("ROLE_ADMIN") && !hasRole("ROLE_SUPERVISOR")) {
            throw new UnauthorizedException("You are not authorized to update this task");
        }
        
        // Can only update pending tasks
        if (task.getStatus() != Task.TaskStatus.PENDING && !hasRole("ROLE_ADMIN") && !hasRole("ROLE_SUPERVISOR")) {
            throw new IllegalStateException("Only pending tasks can be updated");
        }
        
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setType(taskDTO.getType());
        task.setPriority(taskDTO.getPriority());
        task.setDate(taskDTO.getDate());
        task.setStartTime(taskDTO.getStartTime());
        task.setEndTime(taskDTO.getEndTime());
        task.setDeadline(taskDTO.getDeadline());
        task.setAttachmentUrl(taskDTO.getAttachmentUrl());
        
        // Admin or supervisor can change the task status
        if (hasRole("ROLE_ADMIN") || hasRole("ROLE_SUPERVISOR")) {
            if (taskDTO.getStatus() != null) {
                task.setStatus(taskDTO.getStatus());
            }
            
            // Admin or supervisor can change the task user
            if (taskDTO.getUserId() != null) {
                User user = userRepository.findById(taskDTO.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
                task.setUser(user);
            }
            
            // Admin or supervisor can change the task supervisor
            if (taskDTO.getSupervisorId() != null) {
                User supervisor = userRepository.findById(taskDTO.getSupervisorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found with id: " + taskDTO.getSupervisorId()));
                task.setSupervisor(supervisor);
            }
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Only task creator or admin can delete the task
        User currentUser = getCurrentUserEntity();
        if (!task.getUser().getId().equals(currentUser.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to delete this task");
        }
        
        // Can only delete pending tasks unless admin
        if (task.getStatus() != Task.TaskStatus.PENDING && !hasRole("ROLE_ADMIN")) {
            throw new IllegalStateException("Only pending tasks can be deleted");
        }
        
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskDTO> getTasksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return taskRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksBySupervisor(Long supervisorId) {
        User supervisor = userRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found with id: " + supervisorId));
        
        return taskRepository.findBySupervisor(supervisor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByDate(LocalDate date) {
        return taskRepository.findByDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDTO approveTask(Long id, CommentDTO commentDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Only supervisor or admin can approve tasks
        if (!hasRole("ROLE_SUPERVISOR") && !hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to approve tasks");
        }
        
        // Can only approve pending tasks
        if (task.getStatus() != Task.TaskStatus.PENDING) {
            throw new IllegalStateException("Only pending tasks can be approved");
        }
        
        task.setStatus(Task.TaskStatus.APPROVED);
        
        // Add comment if provided
        if (commentDTO != null && commentDTO.getContent() != null && !commentDTO.getContent().trim().isEmpty()) {
            User currentUser = getCurrentUserEntity();
            
            Comment comment = new Comment();
            comment.setContent(commentDTO.getContent());
            comment.setTask(task);
            comment.setUser(currentUser);
            
            commentRepository.save(comment);
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    @Transactional
    public TaskDTO rejectTask(Long id, CommentDTO commentDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Only supervisor or admin can reject tasks
        if (!hasRole("ROLE_SUPERVISOR") && !hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to reject tasks");
        }
        
        // Can only reject pending tasks
        if (task.getStatus() != Task.TaskStatus.PENDING) {
            throw new IllegalStateException("Only pending tasks can be rejected");
        }
        
        // Comment is required for rejection
        if (commentDTO == null || commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment is required for task rejection");
        }
        
        task.setStatus(Task.TaskStatus.REJECTED);
        
        User currentUser = getCurrentUserEntity();
        
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTask(task);
        comment.setUser(currentUser);
        
        commentRepository.save(comment);
        
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    public boolean isTaskCreatedByCurrentUser(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        
        User currentUser = getCurrentUserEntity();
        return task.getUser().getId().equals(currentUser.getId());
    }

    @Override
    public List<TaskDTO> getCurrentUserTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
        
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<Task> userTasks = taskRepository.findByUser(currentUser);
        return userTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatisticsDTO getTaskStatistics() {
        List<Task> allTasks = taskRepository.findAll();
        
        TaskStatisticsDTO statistics = new TaskStatisticsDTO();
        statistics.setTotalTasksCount(allTasks.size());
        statistics.setCompletedTasksCount((long) allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED).count());
        statistics.setPendingTasksCount((long) allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.PENDING).count());
        statistics.setApprovedTasksCount((long) allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.APPROVED).count());
        statistics.setRejectedTasksCount((long) allTasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.REJECTED).count());
        
        return statistics;
    }

    @Override
    public List<TaskDTO> getRecentTasks(int limit) {
        // For now, we'll get all tasks and limit them
        // In a real application, you'd add a repository method for this
        List<Task> allTasks = taskRepository.findAll();
        List<Task> recentTasks = allTasks.stream()
                .sorted((t1, t2) -> {
                    if (t1.getCreatedAt() == null && t2.getCreatedAt() == null) return 0;
                    if (t1.getCreatedAt() == null) return 1;
                    if (t2.getCreatedAt() == null) return -1;
                    return t2.getCreatedAt().compareTo(t1.getCreatedAt());
                })
                .limit(limit)
                .collect(Collectors.toList());
        
        return recentTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getRelatedTasks(Long taskId) {
        Task currentTask = taskRepository.findById(taskId).orElse(null);
        if (currentTask == null) {
            return new ArrayList<>();
        }
        
        List<Task> relatedTasks = new ArrayList<>();
        
        // Get tasks from same user (creator)
        if (currentTask.getUser() != null) {
            List<Task> userTasks = taskRepository.findByUser(currentTask.getUser());
            relatedTasks.addAll(userTasks);
        }
        
        // Get tasks from same supervisor
        if (currentTask.getSupervisor() != null) {
            List<Task> supervisorTasks = taskRepository.findBySupervisor(currentTask.getSupervisor());
            relatedTasks.addAll(supervisorTasks);
        }
        
        // Remove current task and duplicates
        relatedTasks = relatedTasks.stream()
                .filter(t -> !t.getId().equals(taskId))
                .distinct()
                .limit(10) // Limit to 10 related tasks
                .collect(Collectors.toList());
        
        return relatedTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<TaskDTO>> getTasksByTeam() {
        // Since Task doesn't have a team field, we'll group by user instead
        List<Task> allTasks = taskRepository.findAll();
        
        return allTasks.stream()
                .filter(t -> t.getUser() != null)
                .collect(Collectors.groupingBy(
                    t -> t.getUser().getFullName(),
                    Collectors.mapping(this::convertToDTO, Collectors.toList())
                ));
    }

    @Override
    public List<TaskDTO> getFilteredTasks(Map<String, Object> filters) {
        // This is a simplified implementation
        // In a real application, you would build dynamic queries based on filters
        List<Task> allTasks = taskRepository.findAll();
        
        return allTasks.stream()
                .filter(task -> {
                    if (filters.containsKey("status") && filters.get("status") != null) {
                        try {
                            Task.TaskStatus status = Task.TaskStatus.valueOf(filters.get("status").toString());
                            if (task.getStatus() != status) return false;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    
                    if (filters.containsKey("priority") && filters.get("priority") != null) {
                        try {
                            Task.TaskPriority priority = Task.TaskPriority.valueOf(filters.get("priority").toString());
                            if (task.getPriority() != priority) return false;
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    
                    if (filters.containsKey("assignee") && filters.get("assignee") != null) {
                        try {
                            Long assigneeId = Long.valueOf(filters.get("assignee").toString());
                            if (task.getUser() == null || !task.getUser().getId().equals(assigneeId)) return false;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByUserId(Long userId) {
        List<Task> userTasks = taskRepository.findAll().stream()
                .filter(t -> t.getUser() != null && t.getUser().getId().equals(userId))
                .collect(Collectors.toList());
        
        return userTasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private TaskDTO convertToDTO(Task task) {
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
        
        taskDTO.setUserId(task.getUser().getId());
        taskDTO.setUsername(task.getUser().getUsername());
        taskDTO.setUserFullName(task.getUser().getFullName());
        taskDTO.setUserAvatarUrl(task.getUser().getAvatarUrl());
        
        if (task.getSupervisor() != null) {
            taskDTO.setSupervisorId(task.getSupervisor().getId());
            taskDTO.setSupervisorUsername(task.getSupervisor().getUsername());
            taskDTO.setSupervisorFullName(task.getSupervisor().getFullName());
        }
        
        return taskDTO;
    }


    private Task convertToEntity(TaskDTO taskDTO) {
        Task task = modelMapper.map(taskDTO, Task.class);
        
        if (taskDTO.getUserId() != null) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
            task.setUser(user);
        }
        
        if (taskDTO.getSupervisorId() != null) {
            User supervisor = userRepository.findById(taskDTO.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found with id: " + taskDTO.getSupervisorId()));
            task.setSupervisor(supervisor);
        }
        
        return task;
    }


    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // getName() returns the email (username)
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }



    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }
}
