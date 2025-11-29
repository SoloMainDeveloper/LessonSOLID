package ru.urfu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.urfu.document.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.server.ExportException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис экспорта
 */
@Service
public class ExportService {
    /**
     * Экспортёры файлов
     */
    private final Map<String, Exporter> exporters;

    /**
     * Директория для экспорта файлов
     */
    private final Path outputDirectory;

    @Autowired
    public ExportService(@Value("${user.home}") String baseExportDir,
                         List<Exporter> exporters) {
         this.exporters = exporters.stream()
              .collect(Collectors.toMap(
                      Exporter::getSupportableFormat,
                      Function.identity()
              ));
         this.outputDirectory = Path.of(baseExportDir, "lessonSOLID");
    }

    /**
     * Экспортировать документ
     * @param document документ
     * @param format формат экспорта
     * @return путь до экспортированного файла
     * @throws ExportException ошибка экспорта
     */
    public Path exportDocument(Document document, String format) throws ExportException {
        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException e) {
            throw new ExportException("Ошибка создания директории: ", e);
        }

        try {
            Exporter exporter = exporters.get(format);
            if(exporter == null) {
                throw new ExportException("Неверный формат");
            }
            Path outputPath = outputDirectory.resolve(document.name()
                    + "." + exporter.getSupportableFormat());
            exporter.export(outputPath, document);
            return outputPath;
        } catch (IOException e) {
            throw new ExportException("Ошибка экспорта: " + e.getMessage());
        }
    }

    /**
     * Получить поддерживаемые форматы экспорта
     */
    public Set<String> getSupportableFormats() {
        return exporters.keySet();
    }
}
