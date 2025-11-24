package ru.urfu.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Path;

/**
 * Экспортёр документов в PDF.
 */
@Component
public class PdfExporter implements Exporter {
    @Override
    public Path export(Path outputDir, ru.urfu.document.Document document)
            throws DocumentException, java.io.IOException {
        Path outputPath = outputDir.resolve(document.name()
                + "." + getSupportableFormat());
        try (FileOutputStream outputStream =
                     new FileOutputStream(outputPath.toString())) {
            Document pdf = new Document();
            PdfWriter.getInstance(pdf, outputStream);

            pdf.open();
            pdf.add(new Paragraph(document.content()));
            pdf.close();
        }
        return outputPath;
    }

    @Override
    public String getSupportableFormat() {
        return "pdf";
    }
}
