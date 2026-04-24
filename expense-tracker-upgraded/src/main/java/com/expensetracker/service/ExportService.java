package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportToCsv(List<Expense> expenses) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos))) {
            writer.writeNext(new String[]{"ID", "Date", "Category", "Description", "Amount"});
            for (Expense e : expenses) {
                writer.writeNext(new String[]{
                    String.valueOf(e.getId()),
                    e.getDate() != null ? e.getDate().toString() : "",
                    e.getCategory(),
                    e.getDescription() != null ? e.getDescription() : "",
                    e.getAmount().toPlainString()
                });
            }
        }
        return baos.toByteArray();
    }

    public byte[] exportToPdf(List<Expense> expenses, String username) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

        Paragraph title = new Paragraph("Expense Report - " + username, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 2f, 3f, 2f});

        // Header row
        BaseColor headerBg = new BaseColor(52, 73, 94);
        String[] headers = {"#", "Date", "Category", "Description", "Amount"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Data rows
        int i = 1;
        for (Expense e : expenses) {
            BaseColor rowBg = (i % 2 == 0) ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
            addCell(table, String.valueOf(i++), cellFont, rowBg, Element.ALIGN_CENTER);
            addCell(table, e.getDate() != null ? e.getDate().toString() : "", cellFont, rowBg, Element.ALIGN_CENTER);
            addCell(table, e.getCategory(), cellFont, rowBg, Element.ALIGN_LEFT);
            addCell(table, e.getDescription() != null ? e.getDescription() : "-", cellFont, rowBg, Element.ALIGN_LEFT);
            addCell(table, "₹" + e.getAmount().toPlainString(), cellFont, rowBg, Element.ALIGN_RIGHT);
        }

        document.add(table);
        document.close();
        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, BaseColor bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}
