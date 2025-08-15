package com.example.taskflow.util;

import com.example.taskflow.dtos.TaskDTO;
import com.example.taskflow.dtos.TaskStatisticsDTO;
import com.example.taskflow.model.Task;
import com.example.taskflow.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class PDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

    public byte[] generateTasksReport(List<TaskDTO> tasks, String reportTitle) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, reportTitle);
        addTasksTable(document, tasks);
        addFooter(document);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generateDashboardReport(TaskStatisticsDTO statistics, List<TaskDTO> recentTasks, 
                                       List<User> activeUsers, String reportTitle) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, reportTitle);
        addStatisticsSection(document, statistics);
        addRecentTasksSection(document, recentTasks);
        addActiveUsersSection(document, activeUsers);
        addFooter(document);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generateTaskDetails(TaskDTO task, List<TaskDTO> relatedTasks) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, "Task Details Report");
        addTaskDetailsSection(document, task);
        if (relatedTasks != null && !relatedTasks.isEmpty()) {
            addRelatedTasksSection(document, relatedTasks);
        }
        addFooter(document);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generateTeamReport(Map<String, List<TaskDTO>> teamTasks, String reportTitle) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document, reportTitle);
        addTeamPerformanceSection(document, teamTasks);
        addFooter(document);
        document.close();

        return baos.toByteArray();
    }

    private void addHeader(Document document, String title) throws DocumentException {
        Paragraph companyName = new Paragraph("TaskFlow Management System", TITLE_FONT);
        companyName.setAlignment(Element.ALIGN_CENTER);
        document.add(companyName);

        Paragraph reportTitle = new Paragraph(title, HEADER_FONT);
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        reportTitle.setSpacingAfter(20);
        document.add(reportTitle);

        Paragraph date = new Paragraph("Generated on: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), SMALL_FONT);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);
    }

    private void addTasksTable(Document document, List<TaskDTO> tasks) throws DocumentException {
        if (tasks == null || tasks.isEmpty()) {
            document.add(new Paragraph("No tasks found.", NORMAL_FONT));
            return;
        }

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        addTableHeader(table, "ID", "Title", "Status", "Priority", "Assignee", "Due Date");

        for (TaskDTO task : tasks) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(task.getId()), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getTitle(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getStatus() != null ? task.getStatus().toString() : "N/A", NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getPriority() != null ? task.getPriority().toString() : "N/A", NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getUserFullName() != null ? task.getUserFullName() : "Unassigned", NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getDeadline() != null ? task.getDeadline().toString() : "No due date", NORMAL_FONT)));
        }

        document.add(table);
    }

    private void addStatisticsSection(Document document, TaskStatisticsDTO statistics) throws DocumentException {
        document.add(new Paragraph("Task Statistics", HEADER_FONT));
        document.add(new Paragraph(" "));

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);

        addStatRow(statsTable, "Total Tasks", String.valueOf(statistics.getTotalTasksCount()));
        addStatRow(statsTable, "Completed Tasks", String.valueOf(statistics.getCompletedTasksCount()));
        addStatRow(statsTable, "Pending Tasks", String.valueOf(statistics.getPendingTasksCount()));
        addStatRow(statsTable, "Approved Tasks", String.valueOf(statistics.getApprovedTasksCount()));
        addStatRow(statsTable, "Rejected Tasks", String.valueOf(statistics.getRejectedTasksCount()));
        
        double completionRate = statistics.getTotalTasksCount() > 0 ? 
            (double) statistics.getCompletedTasksCount() / statistics.getTotalTasksCount() * 100 : 0;
        addStatRow(statsTable, "Completion Rate", String.format("%.1f%%", completionRate));

        document.add(statsTable);
        document.add(new Paragraph(" "));
    }

    private void addRecentTasksSection(Document document, List<TaskDTO> recentTasks) throws DocumentException {
        document.add(new Paragraph("Recent Tasks", HEADER_FONT));
        document.add(new Paragraph(" "));

        if (recentTasks != null && !recentTasks.isEmpty()) {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addTableHeader(table, "Title", "Status", "Assignee", "Created Date");

            for (TaskDTO task : recentTasks) {
                table.addCell(new PdfPCell(new Phrase(task.getTitle(), NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(task.getStatus() != null ? task.getStatus().toString() : "N/A", NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(task.getUserFullName() != null ? task.getUserFullName() : "Unassigned", NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(task.getCreatedAt() != null ? task.getCreatedAt().toString() : "N/A", NORMAL_FONT)));
            }

            document.add(table);
        } else {
            document.add(new Paragraph("No recent tasks found.", NORMAL_FONT));
        }
        document.add(new Paragraph(" "));
    }

    private void addActiveUsersSection(Document document, List<User> activeUsers) throws DocumentException {
        document.add(new Paragraph("Active Users", HEADER_FONT));
        document.add(new Paragraph(" "));

        if (activeUsers != null && !activeUsers.isEmpty()) {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            addTableHeader(table, "Name", "Email", "Role");

            for (User user : activeUsers) {
                table.addCell(new PdfPCell(new Phrase(user.getFullName(), NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(user.getEmail(), NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(user.getRole().toString(), NORMAL_FONT)));
            }

            document.add(table);
        } else {
            document.add(new Paragraph("No active users found.", NORMAL_FONT));
        }
        document.add(new Paragraph(" "));
    }

    private void addTaskDetailsSection(Document document, TaskDTO task) throws DocumentException {
        document.add(new Paragraph("Task Information", HEADER_FONT));
        document.add(new Paragraph(" "));

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);

        addDetailRow(detailsTable, "Title", task.getTitle());
        addDetailRow(detailsTable, "Description", task.getDescription() != null ? task.getDescription() : "No description");
        addDetailRow(detailsTable, "Status", task.getStatus() != null ? task.getStatus().toString() : "N/A");
        addDetailRow(detailsTable, "Priority", task.getPriority() != null ? task.getPriority().toString() : "N/A");
        addDetailRow(detailsTable, "Assignee", task.getUserFullName() != null ? task.getUserFullName() : "Unassigned");
        addDetailRow(detailsTable, "Due Date", task.getDeadline() != null ? task.getDeadline().toString() : "No due date");
        addDetailRow(detailsTable, "Created Date", task.getCreatedAt() != null ? task.getCreatedAt().toString() : "N/A");
        addDetailRow(detailsTable, "Type", task.getType() != null ? task.getType().toString() : "N/A");

        document.add(detailsTable);
        document.add(new Paragraph(" "));
    }

    private void addRelatedTasksSection(Document document, List<TaskDTO> relatedTasks) throws DocumentException {
        document.add(new Paragraph("Related Tasks", HEADER_FONT));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, "Title", "Status", "Priority", "Due Date");

        for (TaskDTO task : relatedTasks) {
            table.addCell(new PdfPCell(new Phrase(task.getTitle(), NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getStatus() != null ? task.getStatus().toString() : "N/A", NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getPriority() != null ? task.getPriority().toString() : "N/A", NORMAL_FONT)));
            table.addCell(new PdfPCell(new Phrase(task.getDeadline() != null ? task.getDeadline().toString() : "No due date", NORMAL_FONT)));
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addTeamPerformanceSection(Document document, Map<String, List<TaskDTO>> teamTasks) throws DocumentException {
        document.add(new Paragraph("Team Performance Report", HEADER_FONT));
        document.add(new Paragraph(" "));

        for (Map.Entry<String, List<TaskDTO>> entry : teamTasks.entrySet()) {
            String teamName = entry.getKey();
            List<TaskDTO> tasks = entry.getValue();

            document.add(new Paragraph("Team: " + teamName, HEADER_FONT));
            document.add(new Paragraph(" "));

            if (tasks != null && !tasks.isEmpty()) {
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                addTableHeader(table, "Title", "Status", "Assignee", "Priority", "Due Date");

                for (TaskDTO task : tasks) {
                    table.addCell(new PdfPCell(new Phrase(task.getTitle(), NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Phrase(task.getStatus() != null ? task.getStatus().toString() : "N/A", NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Phrase(task.getUserFullName() != null ? task.getUserFullName() : "Unassigned", NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Phrase(task.getPriority() != null ? task.getPriority().toString() : "N/A", NORMAL_FONT)));
                    table.addCell(new PdfPCell(new Phrase(task.getDeadline() != null ? task.getDeadline().toString() : "No due date", NORMAL_FONT)));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("No tasks found for this team.", NORMAL_FONT));
            }
            document.add(new Paragraph(" "));
        }
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addStatRow(PdfPTable table, String label, String value) {
        table.addCell(new PdfPCell(new Phrase(label, NORMAL_FONT)));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(valueCell);
    }

    private void addDetailRow(PdfPTable table, String label, String value) {
        table.addCell(new PdfPCell(new Phrase(label, HEADER_FONT)));
        table.addCell(new PdfPCell(new Phrase(value, NORMAL_FONT)));
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Generated by TaskFlow Management System", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
