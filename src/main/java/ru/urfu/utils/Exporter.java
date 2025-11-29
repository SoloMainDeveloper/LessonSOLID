package ru.urfu.utils;

import ru.urfu.document.Document;

import java.nio.file.Path;

/**
 * Экспортёр документов в файл
 */
public interface Exporter {
    /**
     * Экспортирует содержимое в файл.
     *
     * @param outputPath путь для сохранения файла
     * @param document экспортируемый документ
     * @return путь, куда был экспортирован документ
     * @throws java.io.IOException если не удалось записать файл
     */
    void export(Path outputPath, Document document) throws java.io.IOException;

    /**
     * Возвращает формат, для которого умеет выполнить экспорт.
     * Указывается в нижнем регистре
     */
    String getSupportableFormat();
}
