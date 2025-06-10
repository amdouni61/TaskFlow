package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.CommentDTO;
import com.example.taskflow.dtos.TaskDTO;
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
import java.util.List;
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
        User currentUser = getCurrentUserEntity();
        return taskRepository.findByUser(currentUser).stream()
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
        org.springframework.security.core.userdetails.User userPrincipal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        return userRepository.findByEmail(userPrincipal.getUsername()) // Use email from Spring Security's User principal
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }



    private boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }
}
