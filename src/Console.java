import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Класс-консоль
 */
public class Console {
    Scanner scanner = new Scanner(System.in);
    NoteStorage noteStorage = new NoteStorage();

    /**
     * Конструктор с выводом лого и справки при создании консоли, загрузка записей и запуск приложения
     */
    public Console() {
        System.out.println(
                " ########         #######            \n" +
                "    ##    #####   ##     ##   #####  \n" +
                "    ##   ##   ##  ##     ##  ##   ## \n" +
                "    ##   ##   ##  ##     ##  ##   ## \n" +
                "    ##    #####   #######     #####  \n"
        );
        System.out.println(
                "Справка:                            \n" +
                        "#write - добавить задачу            \n" +
                        "#read - посмотреть внесённые задачи \n" +
                        "#delete - удалить задачу            \n" +
                        "#edit - изменить задачу             \n" +
                        "#search_for_date - поиск по дате     \n" +
                        "#search_for_task - поиск по задаче   \n" +
                        "#statistics - вывод статистики      \n" +
                        "#exit - выход                       "
        );

        noteStorage.loadNotesFromFile(); // загрузка из файла
        run();
    }

    /**
     * Приложение
     *
     * @throws InputMismatchException Если вместо числа введена строка
     */
    private void run() throws InputMismatchException {
        while (true) {
            System.out.println("\nВведите команду: ");
            switch (scanner.nextLine()) {
                case "#write": // Добавить задачу
                    System.out.println("Введите задачу: ");
                    noteStorage.addNote(scanner.nextLine());
                    break;

                case "#read": // Вывести все задачи
                    System.out.println("Внесённые задачи: ");
                    noteStorage.displayNotes(false);
                    break;

                case "#delete": // Удалить задачу
                    try {
                        noteStorage.displayNotes(true);
                        if (noteStorage.isEmpty())
                            break;
                        System.out.println("Введите номер удаляемой задачи: ");
                        noteStorage.deleteNote(scanner.nextInt());
                        scanner.nextLine(); // Чтобы не перескакивало "введите команду"
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Неверный формат ввода");
                    }

                case "#edit": // Редактировать задачу
                    try {
                        noteStorage.displayNotes(true);
                        if (noteStorage.isEmpty())
                            break;
                        System.out.println("Введите номер изменяемой задачи: ");
                        int index = scanner.nextInt();
                        System.out.println("Введите заменяющую задачу: ");
                        scanner.nextLine(); // Чтобы дальше произошел ввод
                        String newTask = scanner.nextLine();
                        noteStorage.editNote(index, newTask);
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Неверный формат ввода");
                    }

                case "#search_for_date": // Поиск по дате
                    System.out.println("Введите дату (дд-ММ-гггг) и/или время (ЧЧ:ММ), разделяя пробелом: ");
                    noteStorage.searchNoteForDateAndOrTime(scanner.nextLine());
                    break;

                case "#search_for_task": // Поиск по задаче
                    System.out.println("Введите задачу: ");
                    noteStorage.searchNoteForTask(scanner.nextLine());
                    break;

                case "#statistics": // Вывод статистики
                    System.out.println("Статистика: ");
                    noteStorage.printStatistics();
                    break;

                case "#exit": // Выход
                    System.out.println("Выход...");
                    System.exit(0);
                    break;

                default: // Если команда неверно введена
                    System.out.println("Команда не распознана");
                    break;
            }
        }
    }
}
