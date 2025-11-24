package ru.urfu;

import com.itextpdf.text.DocumentException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import ru.urfu.document.Document;
import ru.urfu.document.DocumentService;
import ru.urfu.utils.Exporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Основной класс консольного приложения.
 * Реализует ввод команд и взаимодействие с сервисами.
 * Для добавления нового формата экспорта необходимо совершить 1 изменение:
 * Создать класс, имплементирующий интерфейс {@link Exporter}, реализовать его
 * методы и аннотировать класс как @Component
 */
@SpringBootApplication
public class ConsoleApp implements CommandLineRunner {
    /**
     * Директория для экспортируемых файлов
     */
    public static final Path OUTPUT_DIR = Path.of(
            System.getProperty("user.home"), "lessonSOLID");

    /**
     * Сервис для управления документами
     */
    private final DocumentService documentService;

    /**
     * Сканер консольного ввода
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Экспортёры файлов
     */
    private final Map<String, Exporter> exporters;

    @Autowired
    public ConsoleApp(DocumentService documentService, List<Exporter> exporters) {
        this.documentService = documentService;
        this.exporters = exporters.stream()
                .collect(Collectors.toMap(
                        Exporter::getSupportableFormat,
                        Function.identity()
                ));
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

        documentService.saveDocument(new Document(name, content.toString()));
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
            documentService.importTxt(path);
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
     * <p>Логическая ошибки</p>
     * <li>Неправильный JavaDOC</li>
     * <li>Жёсткая завязка на реализацию - нарушение DIP - применяем интерфейс в
     * точке расширения, тем самым соблюдая OCP</li>
     * <li>Размазанная ответственность - лишь метод экспорта на 100% знает, куда он
     * экспортировал файл</li>
     */
    private void exportDocument() {
        System.out.print("Введите номер документа: ");
        int index = Integer.parseInt(scanner.nextLine());

        Optional<Document> documentOptional = documentService.getDocument(index);
        if (documentOptional.isEmpty()) {
            System.out.println("Нет документа с таким номером.");
            return;
        }

        String supportableFormats = String.join("/", exporters.keySet());
        System.out.printf("Введите формат (%s):", supportableFormats);
        String format = scanner.nextLine().trim().toLowerCase();

        try {
            Files.createDirectories(OUTPUT_DIR);
        } catch (IOException e) {
            System.out.println("Ошибка создания директории: " + e);
            return;
        }

        Document document = documentOptional.get();
        try {
            Exporter exporter = exporters.get(format);
            if(exporter == null) {
                System.out.println("Неверный формат");
                return;
            }
            Path outputPath = exporter.export(OUTPUT_DIR, document);
            System.out.println("Экспорт выполнен: " + outputPath);
        } catch (IOException | DocumentException e) {
            System.out.println("Ошибка экспорта: " + e.getMessage());
        }
    }
}
