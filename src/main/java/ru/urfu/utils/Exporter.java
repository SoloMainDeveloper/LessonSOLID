package ru.urfu.utils;

import com.itextpdf.text.DocumentException;
import ru.urfu.document.Document;

import java.nio.file.Path;

/**
 * Экспортёр документов в файл
 */
public interface Exporter {
    /**
     * Экспортирует содержимое в файл.
     *
     * @param outputDir директория для сохранения файла
     * @param document экспортируемый документ
     * @return путь, куда был экспортирован документ
     * @throws DocumentException   если произошла ошибка генерации
     * @throws java.io.IOException если не удалось записать файл
     */
    Path export(Path outputDir, Document document)
            throws DocumentException, java.io.IOException;

    /**
     * Возвращает формат, для которого умеет выполнить экспорт
     */
    String getSupportableFormat();
}
