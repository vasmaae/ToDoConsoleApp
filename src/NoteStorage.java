import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс-хранилище записей
 */
public class NoteStorage {
    LinkedList<Note> notes = new LinkedList<>();
    String filePath = "notes.txt";

    /**
     * Проверяет пусто ли хранилище
     *
     * @return true - пусто, false - не пусто
     */
    public boolean isEmpty() {
        if (notes.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Добавляет запись
     *
     * @param task Добавляемая запись
     */
    public void addNote(String task) {
        Note note = new Note(task);
        notes.addLast(note);
        saveNotesInFile(note);
    }

    /**
     * Выводит все записи в консоль
     *
     * @param withCounter Счетчик записей слева
     */
    public void displayNotes(boolean withCounter) {
        if (notes.isEmpty()) {
            System.out.println("Задач ещё не внесено");
        }
        if (withCounter) {
            int counter = 1;
            for (Note note : notes) {
                System.out.println(counter + ". " + note);
                counter++;
            }
        } else {
            for (Note note : notes) {
                System.out.println(note);
            }
        }
    }

    /**
     * Удаляет запись
     *
     * @param index Индекс удаляемой записи
     */
    public void deleteNote(int index) {
        if (index > 0 && index <= notes.size()) {
            notes.remove(index - 1);
        }
        deleteNoteInFile(index - 1);
    }

    /**
     * Реадктирует запись
     *
     * @param index Индекс редактируемой записи
     * @param task  Задачу, на которую идёт замена
     */
    public void editNote(int index, String task) {
        if (index > 0 && index <= notes.size()) {
            notes.get(index - 1).setTask(task);
        }
        editNoteInFile(index - 1, task);
    }

    /**
     * Поиск по дате и времени
     *
     * @param dateAndOrTime Искомые дата и время
     */
    public void searchNoteForDateAndOrTime(String dateAndOrTime) {
        List<Note> searchedNotes = notes.stream()
                .filter(note -> note.getDateAndTime().contains(dateAndOrTime))
                .toList();
        for (Note searchedNote : searchedNotes) {
            System.out.println(searchedNote);
        }
    }

    /**
     * Поиск по задаче
     *
     * @param task Искомая задача
     */
    public void searchNoteForTask(String task) {
        List<Note> searchedNotes = notes.stream()
                .filter(note -> note.getTask().contains(task))
                .toList();
        for (Note searchedNote : searchedNotes) {
            System.out.println(searchedNote);
        }
    }

    /**
     * Выводит статистику в консоль
     */
    public void printStatistics() {
        int size = notes.size();
        int countOfSymbols = notes.stream()
                .map(Note::getTask)
                .mapToInt(String::length)
                .sum();
        Optional<Map.Entry<String, Long>> mostFrequent = notes.stream()
                .map(Note::getDateAndTime)
                .collect(Collectors.groupingBy(note -> note, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        System.out.println("Количество записей - " + size +
                "\nКоличество символов в контенте - " + countOfSymbols +
                "\nСамый активный день по записям - " + (mostFrequent.isPresent() ? mostFrequent.get().getKey() : 0) +
                ", с числом записей - " + (mostFrequent.isPresent() ? mostFrequent.get().getValue() : 0));
    }

    /**
     * Сохраняет запись в файл
     *
     * @param note Сохраняемая запись
     */
    private void saveNotesInFile(Note note) {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                Files.createFile(Paths.get(filePath));
            }
            Files.write(Paths.get(filePath), (Files.readString(Paths.get(filePath), StandardCharsets.UTF_8)
                    + note.toString() + System.lineSeparator()).getBytes());
        } catch (IOException e) {
            System.out.println("Ошибка при изменении файла");
        }
    }

    /**
     * Удаляет запись из файла
     *
     * @param index Индекс удаляемой записи
     */
    private void deleteNoteInFile(int index) {
        try {
            List<String> tasks = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            tasks.remove(index);
            Files.write(Paths.get(filePath), (String.join(System.lineSeparator(), tasks) + System.lineSeparator()).getBytes());
        } catch (IOException e) {
            System.out.println("Ошибка при изменении файла");
        }
    }

    /**
     * Редактирет запись в файле
     *
     * @param index Индекс редактируемой записи
     * @param task Задача, кна которую идёт замена
     */
    private void editNoteInFile(int index, String task) {
        try {
            List<String> tasks = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            tasks.set(index, new Note(tasks.get(index).split(" - ")[0], task).toString()); // Замена строки по индексу на новую строку из прошлого dataAndTime и новым task
            Files.write(Paths.get(filePath), (String.join(System.lineSeparator(), tasks) + System.lineSeparator()).getBytes());
        } catch (IOException e) {
            System.out.println("Ошибка при изменении файла");
        }
    }

    /**
     * Загрузка записей из файла
     */
    public void loadNotesFromFile() {
        try {
            if (Files.exists(Paths.get(filePath))) {
                List<String> tasks = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
                for (String task : tasks) {
                    notes.addLast(new Note(task.split(" - ")[0], task.split(" - ")[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Файла для загрузки нет");
        }
    }

    /**
     * Класс-запись
     */
    private static class Note {
        private String task;
        private String dateAndTime;

        String getTask() {
            return task;
        }

        String getDateAndTime() {
            return dateAndTime;
        }

        void setTask(String task) {
            this.task = task;
        }

        Note(String note) {
            this.task = note;
            this.dateAndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        }

        Note(String dateAndTime, String note) {
            this.dateAndTime = dateAndTime;
            this.task = note;
        }

        @Override
        public String toString() {
            return dateAndTime + " - " + task;
        }
    }
}
