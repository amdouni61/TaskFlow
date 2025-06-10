package com.example.taskflow.service;

import com.example.taskflow.dtos.ReportDTO;
import com.example.taskflow.dtos.TaskStatisticsDTO;

import java.time.LocalDate;


public interface ReportService {


    TaskStatisticsDTO getTaskStatistics();


    ReportDTO getUserReport(Long userId, LocalDate startDate, LocalDate endDate);


    ReportDTO getCurrentUserReport(LocalDate startDate, LocalDate endDate);


    byte[] generateUserReportPdf(Long userId, LocalDate startDate, LocalDate endDate);


    byte[] generateCurrentUserReportPdf(LocalDate startDate, LocalDate endDate);
}
