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
    /**
     * Список документов
     */
    private final List<Document> documents = new ArrayList<>();

    /**
     * Импортирует текстовый файл и добавляет его как документ в память.
     * <p>Логическая ошибка: неуместный вывод в консоль. Это не задача данного метода</p>
     *
     * @param path путь к txt файлу
     * @throws IOException если файл не найден или не удаётся прочитать
     */
    public void importTxt(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + path);
        }

        String content = Files.readString(path);
        documents.add(new Document(path.getFileName().toString(), content));
    }

    /**
     * Возвращает список всех импортированных документов.
     * <p>Логическая ошибка: неправильное наименование метода</p>
     */
    public List<Document> getDocumentList() {
        return Collections.unmodifiableList(documents);
    }

    /**
     * Возвращает документ по индексу.
     *
     * <p>Логическая ошибка: излишняя проверка на границы List, так как в самом методе
     * get происходит данная проверка</p>
     *
     * @param index индекс документа
     * @return Optional с документом или пустой Optional, если индекс неверен
     */
    public Optional<Document> getDocument(int index) {
        try {
            return Optional.of(documents.get(index));
        } catch(IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Сохраняет документ в памяти.
     * Логическая ошибка: перегруз по функционалу, нарушен SRP
     */
    public void saveDocument(Document document) {
        documents.add(document);
    }

}