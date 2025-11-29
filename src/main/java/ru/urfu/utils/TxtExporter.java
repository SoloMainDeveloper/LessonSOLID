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
    public void export(Path outputPath, Document document) throws IOException {
        Files.writeString(outputPath, document.content());
    }

    @Override
    public String getSupportableFormat() {
        return "txt";
    }
}
