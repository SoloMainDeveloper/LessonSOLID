package ru.urfu.document;

import org.springframework.stereotype.Service;

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
     * Возвращает список всех импортированных документов.
     * <p>Ошибка: неправильное наименование метода</p>
     */
    public List<Document> getDocumentList() {
        return Collections.unmodifiableList(documents);
    }

    /**
     * Возвращает документ по индексу.
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
     * Добавляет документ в список документов.
     * Логическая ошибка: перегруз по функционалу. Метод должен отвечать только за
     * добавление, но не за создание документа.
     */
    public void add(Document document) {
        documents.add(document);
    }

}