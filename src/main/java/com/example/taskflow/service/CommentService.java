package com.example.taskflow.service;

import com.example.taskflow.dtos.CommentDTO;

import java.util.List;


public interface CommentService {


    List<CommentDTO> getAllComments();


    CommentDTO getCommentById(Long id);


    CommentDTO createComment(CommentDTO commentDTO);


    CommentDTO updateComment(Long id, CommentDTO commentDTO);


    void deleteComment(Long id);


    List<CommentDTO> getCommentsByTask(Long taskId);


    List<CommentDTO> getCommentsByUser(Long userId);


    boolean isCommentCreatedByCurrentUser(Long commentId);
}
