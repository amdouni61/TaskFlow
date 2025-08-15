package com.example.taskflow.service.imp;


import com.example.taskflow.dtos.CommentDTO;
import com.example.taskflow.exceptions.ResourceNotFoundException;
import com.example.taskflow.exceptions.UnauthorizedException;
import com.example.taskflow.model.Comment;
import com.example.taskflow.model.Task;
import com.example.taskflow.model.User;
import com.example.taskflow.repositories.CommentRepository;
import com.example.taskflow.repositories.TaskRepository;
import com.example.taskflow.repositories.UserRepository;
import com.example.taskflow.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return convertToDTO(comment);
    }

    @Override
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {
        User currentUser = getCurrentUserEntity();
        Task task = taskRepository.findById(commentDTO.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + commentDTO.getTaskId()));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setTask(task);
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Only comment creator or admin can update the comment
        User currentUser = getCurrentUserEntity();
        if (!comment.getUser().getId().equals(currentUser.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());

        Comment updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Only comment creator or admin can delete the comment
        User currentUser = getCurrentUserEntity();
        if (!comment.getUser().getId().equals(currentUser.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.deleteById(id);
    }

    @Override
    public List<CommentDTO> getCommentsByTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        return commentRepository.findByTask(task).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return commentRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCommentCreatedByCurrentUser(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User currentUser = getCurrentUserEntity();
        return comment.getUser().getId().equals(currentUser.getId());
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);

        commentDTO.setTaskId(comment.getTask().getId());
        commentDTO.setUserId(comment.getUser().getId());
        commentDTO.setUsername(comment.getUser().getUsername());
        commentDTO.setUserFullName(comment.getUser().getFullName());
        commentDTO.setUserAvatarUrl(comment.getUser().getAvatarUrl());

        return commentDTO;
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
