package com.example.taskflow.repositories;

import com.example.taskflow.model.Task;
import com.example.taskflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    //Find all tasks created by a user.

    List<Task> findByUser(User user);
    //Find all tasks supervised by a user.
    List<Task> findBySupervisor(User supervisor);

    //Find all tasks by status.

    List<Task> findByStatus(Task.TaskStatus status);

    //Find all tasks by type.

    List<Task> findByType(Task.TaskType type);


     //Find all tasks by date.
    List<Task> findByDate(LocalDate date);

   //Find all tasks by date between start and end date.

    List<Task> findByDateBetween(LocalDate startDate, LocalDate endDate);

    //Find all tasks by user and status.

    List<Task> findByUserAndStatus(User user, Task.TaskStatus status);

     //Find all tasks by supervisor and status.

    List<Task> findBySupervisorAndStatus(User supervisor, Task.TaskStatus status);

     //Find all tasks by user and date.

    List<Task> findByUserAndDate(User user, LocalDate date);

    //Count tasks by status.

    long countByStatus(Task.TaskStatus status);

    //Count tasks by user and status.

    long countByUserAndStatus(User user, Task.TaskStatus status);

    //Get task distribution by user.

    @Query("SELECT t.user.id, COUNT(t) FROM Task t GROUP BY t.user.id")
    List<Object[]> getTaskDistributionByUser();

   //Get task distribution by status.

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> getTaskDistributionByStatus();

      //Get task distribution by type.

    @Query("SELECT t.type, COUNT(t) FROM Task t GROUP BY t.type")
    List<Object[]> getTaskDistributionByType();

    List<Task> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
