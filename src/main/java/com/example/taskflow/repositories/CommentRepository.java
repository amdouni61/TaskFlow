package com.example.taskflow.repositories;

import com.example.taskflow.model.Comment;
import com.example.taskflow.model.Task;
import com.example.taskflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

     //Find all comments on a task.

    List<Comment> findByTask(Task task);

    //Find all comments by a user.

    List<Comment> findByUser(User user);

     //Find all comments by a user on a task.

    List<Comment> findByUserAndTask(User user, Task task);
}
