package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.ReportDTO;
import com.example.taskflow.dtos.TaskDTO;
import com.example.taskflow.dtos.TaskStatisticsDTO;
import com.example.taskflow.exceptions.ResourceNotFoundException;
import com.example.taskflow.model.Task;
import com.example.taskflow.model.User;
import com.example.taskflow.repositories.TaskRepository;
import com.example.taskflow.repositories.UserRepository;
import com.example.taskflow.service.ReportService;
import com.example.taskflow.service.TaskService;
import com.example.taskflow.util.PDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PDFGenerator pdfGenerator;

    @Override
    public TaskStatisticsDTO getTaskStatistics() {
        TaskStatisticsDTO statistics = new TaskStatisticsDTO();

        // Task count by status
        statistics.setPendingTasksCount(taskRepository.countByStatus(Task.TaskStatus.PENDING));
        statistics.setApprovedTasksCount(taskRepository.countByStatus(Task.TaskStatus.APPROVED));
        statistics.setRejectedTasksCount(taskRepository.countByStatus(Task.TaskStatus.REJECTED));
        statistics.setCompletedTasksCount(taskRepository.countByStatus(Task.TaskStatus.COMPLETED));
        statistics.setTotalTasksCount(taskRepository.count());

        // Task distribution by user
        Map<String, Long> taskCountByUser = new HashMap<>();
        List<Object[]> userTaskDistribution = taskRepository.getTaskDistributionByUser();
        for (Object[] result : userTaskDistribution) {
            Long userId = (Long) result[0];
            Long count = (Long) result[1];

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

            taskCountByUser.put(user.getFullName(), count);
        }
        statistics.setTaskCountByUser(taskCountByUser);

        // Task distribution by type
        Map<Task.TaskType, Long> taskCountByType = new HashMap<>();
        List<Object[]> typeTaskDistribution = taskRepository.getTaskDistributionByType();
        for (Object[] result : typeTaskDistribution) {
            Task.TaskType type = (Task.TaskType) result[0];
            Long count = (Long) result[1];

            taskCountByType.put(type, count);
        }
        statistics.setTaskCountByType(taskCountByType);

        // Task distribution by status
        Map<Task.TaskStatus, Long> taskCountByStatus = new HashMap<>();
        List<Object[]> statusTaskDistribution = taskRepository.getTaskDistributionByStatus();
        for (Object[] result : statusTaskDistribution) {
            Task.TaskStatus status = (Task.TaskStatus) result[0];
            Long count = (Long) result[1];

            taskCountByStatus.put(status, count);
        }
        statistics.setTaskCountByStatus(taskCountByStatus);

        // Task distribution by month
        Map<String, Long> taskCountByMonth = new HashMap<>();
        LocalDate now = LocalDate.now();

        // Get task counts for the last 12 months
        for (int i = 0; i < 12; i++) {
            LocalDate date = now.minusMonths(i);
            LocalDate startOfMonth = date.withDayOfMonth(1);
            LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

            List<Task> tasks = taskRepository.findByDateBetween(startOfMonth, endOfMonth);
            String monthName = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            taskCountByMonth.put(monthName + " " + date.getYear(), (long) tasks.size());
        }
        statistics.setTaskCountByMonth(taskCountByMonth);

        return statistics;
    }

    @Override
    public ReportDTO getUserReport(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Get user tasks in date range
        List<Task> tasks = taskRepository.findByUserAndDateBetween(user, startDate, endDate);

        // Convert tasks to DTOs
        List<TaskDTO> taskDTOs = tasks.stream()
                .map(task -> taskService.getTaskById(task.getId()))
                .collect(Collectors.toList());

        // Count tasks by status
        long pendingCount = tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.PENDING).count();
        long approvedCount = tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.APPROVED).count();
        long rejectedCount = tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.REJECTED).count();
        long completedCount = tasks.stream().filter(task -> task.getStatus() == Task.TaskStatus.COMPLETED).count();

        // Create report
        ReportDTO report = new ReportDTO();
        report.setUserId(userId);
        report.setUsername(user.getUsername());
        report.setUserFullName(user.getFullName());
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTasks(taskDTOs);
        report.setPendingTasksCount(pendingCount);
        report.setApprovedTasksCount(approvedCount);
        report.setRejectedTasksCount(rejectedCount);
        report.setCompletedTasksCount(completedCount);
        report.setTotalTasksCount(taskDTOs.size());

        return report;
    }

    @Override
    public ReportDTO getCurrentUserReport(LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentUserEntity();
        return getUserReport(currentUser.getId(), startDate, endDate);
    }

    @Override
    public byte[] generateUserReportPdf(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            ReportDTO report = getUserReport(userId, startDate, endDate);
            // Convert ReportDTO to TaskDTO list for the new PDF generator
            List<TaskDTO> tasks = report.getTasks();
            return pdfGenerator.generateTasksReport(tasks, "User Report - " + report.getUserFullName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] generateCurrentUserReportPdf(LocalDate startDate, LocalDate endDate) {
        try {
            ReportDTO report = getCurrentUserReport(startDate, endDate);
            // Convert ReportDTO to TaskDTO list for the new PDF generator
            List<TaskDTO> tasks = report.getTasks();
            return pdfGenerator.generateTasksReport(tasks, "Current User Report - " + report.getUserFullName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

    // Get current user entity
    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        } else {
            throw new ResourceNotFoundException("Invalid authentication principal");
        }
    }
}

