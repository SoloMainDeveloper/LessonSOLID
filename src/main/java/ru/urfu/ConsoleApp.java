package ru.urfu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import ru.urfu.document.Document;
import ru.urfu.document.DocumentService;
import ru.urfu.utils.ExportService;
import ru.urfu.utils.Exporter;
import ru.urfu.utils.ImportService;

import java.io.IOException;
import java.nio.file.Path;
import java.rmi.server.ExportException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Основной класс консольного приложения.
 * Реализует ввод команд и взаимодействие с сервисами.
 * Для добавления нового формата экспорта необходимо совершить 1 изменение:
 * Создать класс, имплементирующий интерфейс {@link Exporter}, реализовать его
 * методы и аннотировать класс как @Component
 *
 * <p>Ошибки в проекте</p>
 * <ol>
 * <li>Коллективная безответственность: импорт не должен выводить информацию в
 * консоль. Взаимодействие с пользователем должен реализовывать класс ConsoleApp</li>
 * <li>Нарушен принцип DIP: ConsoleApp зависит от низкоуровневых реализаций экспорта.
 * Решение: создать интерфейс Exporter</li>
 * <li>Но если создать Exporter тогда нарушим SRP в ConsoleApp, так как он теперь отвечать
 * ещё и за управление экспортёрами. Решение: выделить задачу экспорта в отдельный
 * сервис {@link ExportService}</li>
 * <li>В DocumentService нарушен SRP: сервис должен отвечать за взаимодействие с
 * документами, но никак не за из импорт. Решение: создать {@link ImportService} и
 * взаимодействовать с ним через ConsoleApp</li>
 * <li>Размазанная ответственность и нарушение SRP: ConsoleApp не должен заниматься
 * формированием пути экспорта. Решение: этим занимается ExportService. Он же возвращает
 * путь, куда он экспортировал файл.</li>
 * </ol>
 */
@SpringBootApplication
public class ConsoleApp implements CommandLineRunner {
    /**
     * Сервис для управления документами
     */
    private final DocumentService documentService;

    /**
     * Сервис импорта файлов
     */
    private final ImportService importService = new ImportService();

    /**
     * Сервис экспорта файлов
     */
    private final ExportService exportService;

    /**
     * Сканер консольного ввода
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Конструктор
     */
    @Autowired
    public ConsoleApp(DocumentService documentService, ExportService exportService) {
        this.documentService = documentService;
        this.exportService = exportService;
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
                case "list" -> printListDocuments();
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
            if (line.isEmpty()) {
                break; // окончание ввода
            }
            content.append(line).append(System.lineSeparator());
        }

        documentService.add(new Document(name, content.toString()));
        System.out.println("Документ создан и сохранён в памяти.");
    }

    /**
     * Выполняет импорт документа. Для этого запрашивает у пользователя путь к файлу
     */
    private void importDocument() {
        System.out.print("Введите путь к txt файлу: ");
        String pathStr = scanner.nextLine();
        Path path = Path.of(pathStr);
        try {
            Document document = importService.importTxt(path);
            documentService.add(document);
            System.out.println("Документ импортирован: " + path.getFileName());
        } catch (IOException e) {
            System.out.println("Ошибка импорта: " + e.getMessage());
        }
    }

    /**
     * Выводит в консоль список документов
     * <p>Логическая ошибки: неправильное наименование метода и JavaDOC</p>
     */
    private void printListDocuments() {
        List<Document> documents = documentService.getDocumentList();
        if (documents.isEmpty()) {
            System.out.println("Документов нет");
            return;
        }
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            System.out.println(i + ": " + doc.name());
        }
    }

    /**
     * Выполняет экспорт документа в выходную директорию. Для этого запрашивает у
     * пользователя номер документа и желаемый формат экспорта.
     */
    private void exportDocument() {
        System.out.print("Введите номер документа: ");
        int index = Integer.parseInt(scanner.nextLine());

        Optional<Document> documentOptional = documentService.getDocument(index);
        if (documentOptional.isEmpty()) {
            System.out.println("Нет документа с таким номером.");
            return;
        }

        Set<String> supportableFormats = exportService.getSupportableFormats();
        System.out.printf("Введите формат (%s):", String.join("/", supportableFormats));
        Document document = documentOptional.get();
        String format = scanner.nextLine().trim().toLowerCase();

        try {
            Path outputPath = exportService.exportDocument(document, format);
            System.out.println("Экспорт выполнен: " + outputPath);
        } catch (ExportException e) {
            System.out.println(e.getMessage());
        }
    }
}
