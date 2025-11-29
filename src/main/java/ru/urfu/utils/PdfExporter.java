package ru.urfu.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Экспортёр документов в PDF.
 */
@Component
public class PdfExporter implements Exporter {
    @Override
    public void export(Path outputPath, ru.urfu.document.Document document)
            throws java.io.IOException {

        try (FileOutputStream outputStream =
                     new FileOutputStream(outputPath.toString())) {
            Document pdf = new Document();
            PdfWriter.getInstance(pdf, outputStream);

            pdf.open();
            pdf.add(new Paragraph(document.content()));
            pdf.close();
        } catch (DocumentException e) {
            throw new IOException("Ошибка генерации PDF", e);
        }
    }

    @Override
    public String getSupportableFormat() {
        return "pdf";
    }
}
