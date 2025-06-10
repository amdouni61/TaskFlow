package com.example.taskflow.conrollers;

import com.example.taskflow.dtos.ReportDTO;
import com.example.taskflow.dtos.TaskStatisticsDTO;
import com.example.taskflow.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    //Get task statistics.

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<TaskStatisticsDTO> getTaskStatistics() {
        return ResponseEntity.ok(reportService.getTaskStatistics());
    }

   //Get user report.

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or @userService.isCurrentUser(#userId)")
    public ResponseEntity<ReportDTO> getUserReport(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getUserReport(userId, startDate, endDate));
    }

    //Get current user report.

    @GetMapping("/me")
    public ResponseEntity<ReportDTO> getCurrentUserReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getCurrentUserReport(startDate, endDate));
    }

    //Generate PDF report for a user.

    @GetMapping("/user/{userId}/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR') or @userService.isCurrentUser(#userId)")
    public ResponseEntity<byte[]> generateUserReportPdf(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdfBytes = reportService.generateUserReportPdf(userId, startDate, endDate);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=user-report.pdf")
                .body(pdfBytes);
    }

    //Generate PDF report for current user.

    @GetMapping("/me/pdf")
    public ResponseEntity<byte[]> generateCurrentUserReportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        byte[] pdfBytes = reportService.generateCurrentUserReportPdf(startDate, endDate);
        return ResponseEntity
                .ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=my-report.pdf")
                .body(pdfBytes);
    }
}
