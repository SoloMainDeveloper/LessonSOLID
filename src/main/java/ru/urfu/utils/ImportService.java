package ru.urfu.utils;

import ru.urfu.document.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Сервис импорта
 */
public class ImportService {
    /**
     * Импортирует текстовый файл и добавляет его как документ в память.
     * @param path путь к txt файлу
     * @throws IOException если файл не найден или не удаётся прочитать
     */
    public Document importTxt(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + path);
        }

        String content = Files.readString(path);
        return new Document(path.getFileName().toString(), content);
    }
}
