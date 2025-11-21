package ru.urfu;

import com.itextpdf.text.DocumentException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import ru.urfu.document.Document;
import ru.urfu.document.DocumentService;
import ru.urfu.utils.PdfExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Основной класс консольного приложения.
 * Реализует ввод команд и взаимодействие с сервисами.
 */
@SpringBootApplication
public class ConsoleApp implements CommandLineRunner {

    public static final Path OUTPUT_DIR = Path.of(System.getProperty("user.home"), "lessonSOLID");

    private final DocumentService documentService;

    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public ConsoleApp(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Точка входа приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ConsoleApp.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Консольное приложение ===");

        while (true) {
            System.out.println("\nКоманды: import, list, create, export, exit");
            System.out.print("> ");
            String cmd = scanner.nextLine().trim();

            switch (cmd) {
                case "create" -> createDocument();
                case "import" -> importDocument();
                case "list" -> listDocuments();
                case "export" -> exportDocument();
                case "exit" -> {
                    return;
                }
                default -> System.out.println("Неизвестная команда");
            }
        }
    }

    /**
     * Создаёт документ через ввод данных в консоли.
     * Сначала пользователь вводит имя, затем — содержимое документа.
     * Ввод содержимого продолжается до пустой строки.
     */
    private void createDocument() {
        System.out.print("Введите имя документа: ");
        String name = scanner.nextLine().trim();

        System.out.println("Введите содержимое документа (пустая строка — завершить ввод):");

        StringBuilder content = new StringBuilder();
        while (true) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break; // окончание ввода
            content.append(line).append(System.lineSeparator());
        }

        documentService.createDocument(name, content.toString());

        System.out.println("Документ создан и сохранён в памяти.");
    }

    /**
     * Выполняет импорт документа.
     */
    private void importDocument() {
        System.out.print("Введите путь к txt файлу: ");
        String path = scanner.nextLine();

        try {
            documentService.importTxt(path);
        } catch (IOException e) {
            System.out.println("Ошибка импорта: " + e.getMessage());
        }
    }

    /**
     * Выполняет импорт документа.
     */
    private void listDocuments() {
        List<Document> documents = documentService.list();
        if (documents.isEmpty()) {
            System.out.println("Документов нет");
            return;
        }
        int i = 0;
        for (Document doc : documents) {
            System.out.println(i + ": " + doc.name());
            i++;
        }
    }

    /**
     * Выполняет импорт документа.
     */
    private void exportDocument() {
        System.out.print("Введите номер документа: ");
        int index = Integer.parseInt(scanner.nextLine());

        Optional<Document> documentOptional = documentService.getDocument(index);
        if (documentOptional.isEmpty()) {
            System.out.println("Нет документа с таким номером.");
            return;
        }

        Document document = documentOptional.get();

        System.out.print("Введите формат (txt/pdf): ");
        String format = scanner.nextLine().trim().toLowerCase();

        try {
            Files.createDirectories(OUTPUT_DIR);
        } catch (IOException e) {
            System.out.println("Ошибка создания директории: " + e);
            return;
        }

        Path outputPath = OUTPUT_DIR.resolve(document.name() + "." + format);

        try {
            switch (format) {
                case "txt" -> Files.writeString(outputPath, document.content());
                case "pdf" -> PdfExporter.export(outputPath.toString(), document.content());
                default -> {
                    System.out.println("Неверный формат");
                    return;
                }
            }

            System.out.println("Экспорт выполнен: " + outputPath);
        } catch (IOException | DocumentException e) {
            System.out.println("Ошибка экспорта: " + e.getMessage());
        }
    }
}
