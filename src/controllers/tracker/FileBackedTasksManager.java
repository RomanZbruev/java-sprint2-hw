package controllers.tracker;
import controllers.history.HistoryManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    File historyFile;

    public FileBackedTasksManager(File historyFile) {
        this.historyFile = historyFile;
    }

    @Override
    public void clearTaskStorage() {
        super.clearTaskStorage();
        save();
    }

    @Override
    public void clearSubtaskStorage() {
        super.clearSubtaskStorage();
        save();
    }

    @Override
    public void clearEpicStorage() {
        super.clearEpicStorage();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }
    // методы get также переписываем, так как при их вызове меняется история => сохраняем в файл
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void createNewTask(Task task, String title, String description) {
        super.createNewTask(task, title, description);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask, String title, String description, int yourEpicId) {
        super.createNewSubtask(subtask, title, description, yourEpicId);
        save();
    }

    @Override
    public void createNewEpic(Epic epic, String title, String description) {
        super.createNewEpic(epic, title, description);
        save();
    }

    @Override
    public void updateTask(int id, Task task, String title, String description, Status status) {
        super.updateTask(id, task, title, description, status);
        save();
    }

    @Override
    public void updateSubtask(int id, Subtask subtask, String title,
                              String description, Status status, int yourEpicId) {
        super.updateSubtask(id, subtask, title, description, status, yourEpicId);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic, String title,
                           String description) {
        super.updateEpic(id, epic, title, description);
        save();
    }

    public void save() {
        try {
            Writer fileWriter = new FileWriter(historyFile);
            fileWriter.write("id,type,name,status,description,epic\n"); // заглавная строка
            // записываем все виды задач (эпики записываем перед подзадачами из-за особенностей реализации)
            for (Task task : getTaskList()) {
                fileWriter.write(toString(task));
                fileWriter.write("\n");
            }

            for (Epic task : getEpicList()) {
                fileWriter.write(toString(task));
                fileWriter.write("\n");
            }
            for (Subtask task : getSubtaskList()) {
                fileWriter.write(toString(task, task.getYourEpicId()));
                fileWriter.write("\n");
            }
            fileWriter.write("\n");
            fileWriter.write(toString(historyManager));
            fileWriter.close();
        } catch (IOException exception) { // кидаем сосбвтенное непроверяемое исключение
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }

    }

    // методы toString для всех видов задач
    String toString(Task task) {
        return (task.getId() + "," + Type.TASK + "," + task.getTitle() + "," + task.getStatus()
                + "," + task.getDescription());
    }

    String toString(Epic task) {
        return (task.getId() + "," + Type.EPIC + "," + task.getTitle() + "," + task.getStatus()
                + "," + task.getDescription());
    }

    String toString(Subtask task, int EpicId) {
        return (task.getId() + "," + Type.SUBTASK + "," + task.getTitle() + "," + task.getStatus()
                + "," + task.getDescription() + "," + EpicId);
    }

    // метод восстановления задачи из строки файла
    Task fromString(String value) {
        String[] split = value.split(",");
        if (split.length == 5) { // если в строке 5 элементов - либо задача, либо эпик
            if (split[1].equals(Type.TASK.toString())) {
                Task task = new Task();
                task.setId(Integer.parseInt(split[0]));
                task.setTitle(split[2]);
                task.setStatus(Status.valueOf(split[3]));
                task.setDescription(split[4]);
                return task;
            } else if (split[1].equals(Type.EPIC.toString())) {
                Epic task = new Epic();
                task.setId(Integer.parseInt(split[0]));
                task.setTitle(split[2]);
                task.setStatus(Status.valueOf(split[3]));
                task.setDescription(split[4]);
                return task;
            }
        }
        else if (split.length == 6) {
            Subtask subtask = new Subtask();
            subtask.setId(Integer.parseInt(split[0]));
            subtask.setTitle(split[2]);
            subtask.setStatus(Status.valueOf(split[3]));
            subtask.setDescription(split[4]);
            subtask.setYourEpicId(Integer.parseInt(split[5]));
            return subtask;
        }
        return null;
    }

    // метод записи айди задач из истории в файл
    static String toString(HistoryManager historyManager) {
        List<Task> hm = historyManager.getHistory();
        List<String> ids = new ArrayList<>();
        for (Task task : hm) {
            ids.add(Integer.toString(task.getId()));
        }

        return String.join(",", ids);
    }

    // метод восстановления списка задач из файла
    static List<Integer> fromStringHistoryManager(String value) {
        List<Integer> ids = new ArrayList<>();
        String[] split = value.split(",");
        for (String s : split) {
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }

    // метод восстновления истории

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbtm = new FileBackedTasksManager(file);
        try {
            Path path = file.toPath();
            String ourData = Files.readString(path); // считываем данные
            String[] split = ourData.split("\n"); // разбиваем на строки
            List<String> splitData = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
            List<Integer> history = fromStringHistoryManager(splitData.get(splitData.size() - 1)); //переводим
            // последнюю строку в массив айди для истории
            for (String data : splitData) { //проверям тип задачи, добавляем задачу в hashmap задач, а также
                // айди задачи - в список айди задач данного типа (См.InMemoryTaskManager)
                if (data.contains(Type.SUBTASK.toString())) {
                    Subtask task = (Subtask) fbtm.fromString(data);
                    fbtm.getAllTasks().put(task.getId(),task);
                    fbtm.getSubtaskIds().add(task.getId());
                }
                else if (data.contains(Type.TASK.toString())) {
                    Task task = fbtm.fromString(data);
                    fbtm.getAllTasks().put(task.getId(),task);
                    fbtm.getTaskIds().add(task.getId());
                }
                else if (data.contains(Type.EPIC.toString())) {
                    Epic task = (Epic) fbtm.fromString(data);
                    fbtm.getAllTasks().put(task.getId(),task);
                    fbtm.getEpicIds().add(task.getId());
                }


            }
            for (int i : history) { //восстанавливаем историю с помощью айди подзадач, по очереди добавляя задачи
                if (fbtm.getTaskIds().contains(i)) {
                    fbtm.historyManager.add(fbtm.getTaskById(i));
                } else if (fbtm.getSubtaskIds().contains(i)) {
                    fbtm.historyManager.add(fbtm.getSubtaskById(i));
                } else if (fbtm.getEpicIds().contains(i)) {
                    fbtm.historyManager.add(fbtm.getEpicById(i));
                }
            }
        } catch (IOException e) { // кидаем сосбвтенное непроверяемое исключение
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return fbtm;
    }

    public static void main(String[] args) {
        File file = new File("src/controllers/tracker/history.csv");
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        System.out.println("Создаем две задачи");
        Task task1 = new Task();
        taskManager.createNewTask(task1, "task1", "task1 description");
        Task task2 = new Task();
        taskManager.createNewTask(task2, "task2", "task2 description");
        System.out.println("Создаем эпик с тремя подзадачами");
        Epic epic1 = new Epic();
        taskManager.createNewEpic(epic1, "epic1", "epic1 description");
        Subtask subtask1 = new Subtask();
        taskManager.createNewSubtask(subtask1, "subtask1", "subtask1 description", epic1.getId());
        Subtask subtask2 = new Subtask();
        taskManager.createNewSubtask(subtask2, "subtask2", "subtask2 description", epic1.getId());
        Subtask subtask3 = new Subtask();
        taskManager.createNewSubtask(subtask3, "subtask3", "subtask3 description", epic1.getId());
        System.out.println("Создаем эпик без подзадач");
        Epic epic2 = new Epic();
        taskManager.createNewEpic(epic2, "epic2", "epic2 description");
        System.out.println("--------------");
        System.out.println("Запрашиваем задачи в разном порядке");
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println("История до закрытия:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println("Распечатали списки до закрытия");
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println("--------------");
        FileBackedTasksManager tasksManager2 = loadFromFile(file);
        System.out.println("История из файла:");
        System.out.println(tasksManager2.getHistoryManager().getHistory());
        System.out.println("Распечатали списки из файла");
        System.out.println(tasksManager2.getTaskList());
        System.out.println(tasksManager2.getSubtaskList());
        System.out.println(tasksManager2.getEpicList());
    }
}
