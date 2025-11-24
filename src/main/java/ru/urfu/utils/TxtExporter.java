package ru.urfu.utils;

import org.springframework.stereotype.Component;
import ru.urfu.document.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Экспортёр докуметов в txt
 */
@Component
public class TxtExporter implements Exporter {
    @Override
    public Path export(Path outputDir, Document document) throws IOException {
        Path outputPath = outputDir.resolve(document.name()
                + "." + getSupportableFormat());
        Files.writeString(outputPath, document.content());
        return outputPath;
    }

    @Override
    public String getSupportableFormat() {
        return "txt";
    }
}
