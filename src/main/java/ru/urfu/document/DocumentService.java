package ru.urfu.document;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Сервис для управления документами. Позволяет импортировать файлы
 *  и хранит документы в оперативной памяти.
 */
@Service
public class DocumentService {

    private final List<Document> documents = new ArrayList<>();

    /**
     * Импортирует текстовый файл и добавляет его как документ в память.
     *
     * @param pathStr путь к txt файлу
     * @throws IOException если файл не найден или не удаётся прочитать
     */
    public void importTxt(String pathStr) throws IOException {
        Path path = Path.of(pathStr);

        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + path);
        }

        String content = Files.readString(path);
        documents.add(new Document(path.getFileName().toString(), content));
        System.out.println("Документ импортирован: " + path.getFileName());
    }

    /**
     * Возвращает список всех импортированных документов.
     */
    public List<Document> list() {
        return Collections.unmodifiableList(documents);
    }

    /**
     * Возвращает документ по индексу.
     *
     * @param index индекс документа
     * @return Optional с документом или пустой Optional, если индекс неверен
     */
    public Optional<Document> getDocument(int index) {
        if (index < 0 || index >= documents.size()) {
            return Optional.empty();
        }
        return Optional.of(documents.get(index));
    }

    /**
     * Создаёт документ по имени и содержимому и сохраняет его в памяти.
     *
     * @param name имя документа
     * @param content текстовое содержимое документа
     */
    public void createDocument(String name, String content) {
        documents.add(new Document(name, content));
    }

}