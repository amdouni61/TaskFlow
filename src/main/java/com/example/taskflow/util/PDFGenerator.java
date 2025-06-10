package com.example.taskflow.util;


import com.example.taskflow.dtos.ReportDTO;
import com.example.taskflow.dtos.TaskDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.Stream;

@Component
public class PDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

     //Generate a PDF report for a user.

    public byte[] generateUserReportPdf(ReportDTO report) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Paragraph title = new Paragraph("Task Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Add user info
            document.add(new Paragraph("User: " + report.getUserFullName() + " (" + report.getUsername() + ")", SUBTITLE_FONT));
            document.add(new Paragraph("Period: " + report.getStartDate().format(DATE_FORMAT) + " to " + report.getEndDate().format(DATE_FORMAT), SUBTITLE_FONT));
            document.add(Chunk.NEWLINE);

            // Add task statistics
            document.add(new Paragraph("Task Statistics", SUBTITLE_FONT));
            document.add(new Paragraph("Total Tasks: " + report.getTotalTasksCount(), NORMAL_FONT));
            document.add(new Paragraph("Pending Tasks: " + report.getPendingTasksCount(), NORMAL_FONT));
            document.add(new Paragraph("Approved Tasks: " + report.getApprovedTasksCount(), NORMAL_FONT));
            document.add(new Paragraph("Rejected Tasks: " + report.getRejectedTasksCount(), NORMAL_FONT));
            document.add(new Paragraph("Completed Tasks: " + report.getCompletedTasksCount(), NORMAL_FONT));
            document.add(Chunk.NEWLINE);

            // Add task table
            document.add(new Paragraph("Task List", SUBTITLE_FONT));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Add table header
            Stream.of("Date", "Title", "Type", "Priority", "Status")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, HEADER_FONT));
                        table.addCell(header);
                    });

            // Add task rows
            for (TaskDTO task : report.getTasks()) {
                table.addCell(new Phrase(task.getDate().format(DATE_FORMAT), NORMAL_FONT));
                table.addCell(new Phrase(task.getTitle(), NORMAL_FONT));
                table.addCell(new Phrase(task.getType().toString(), NORMAL_FONT));
                table.addCell(new Phrase(task.getPriority().toString(), NORMAL_FONT));
                table.addCell(new Phrase(task.getStatus().toString(), NORMAL_FONT));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Add footer
            document.add(new Paragraph("Report generated on: " + new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date()), NORMAL_FONT));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }
}
