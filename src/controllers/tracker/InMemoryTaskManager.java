package controllers.tracker;

import controllers.IntersectionCheckException;
import controllers.history.HistoryManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    // Создаем три хранилища для задач разных типов и поле для хранения айди
    private final HashMap<Integer, Task> allTasks = new HashMap<>();
    private final List<Integer> taskIds = new ArrayList<>();
    private final List<Integer> subtaskIds = new ArrayList<>();
    private final List<Integer> epicIds = new ArrayList<>();
    int globalTaskId = 0;
    HistoryManager historyManager = Managers.getDefaultHistory();
    Comparator<Task> comparator = (task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return 0;
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return 1;
        } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return -1;
        } else {
            return 0;
        }
    };

    private final TreeSet<Task> sortedTaskAndSubtasks = new TreeSet<>(comparator);

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    // Методы для получения списка задач разных типов:
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> listOfTask = new ArrayList<>();
        for (Integer id : taskIds) {
            listOfTask.add(allTasks.get(id));
        }
        return listOfTask;
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        for (Integer id : subtaskIds) {
            Subtask subtask = (Subtask) allTasks.get(id);
            listOfSubtask.add(subtask);
        }
        return listOfSubtask;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> listOfEpic = new ArrayList<>();
        for (Integer id : epicIds) {
            Epic epic = (Epic) allTasks.get(id);
            listOfEpic.add(epic);
        }
        return listOfEpic;
    }

    // Методы полной очистки хранилищ задач разных типов:
    @Override
    public void clearTaskStorage() {
        for (Integer id : taskIds) {
            allTasks.remove(id);
            historyManager.remove(id);
        }
        taskIds.clear();
    }

    @Override
    public void clearSubtaskStorage() { // при очистке списка позадач - все эпики переходят в статус "NEW"
        for (Integer id : subtaskIds) {
            allTasks.remove(id);
            historyManager.remove(id);
        }
        subtaskIds.clear();
        for (Integer id : epicIds) {
            allTasks.get(id).setStatus(Status.NEW);
        }
    }

    @Override
    public void clearEpicStorage() {
        for (Integer id : epicIds) {
            allTasks.remove(id);
            historyManager.remove(id);
        }
        epicIds.clear();
    }

    // Методы получения задач по айди:
    @Override
    public Task getTaskById(int id) {
        if (allTasks.containsKey(id)) {
            historyManager.add(allTasks.get(id));
        }
        return allTasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = (Subtask) allTasks.get(id);
        if (allTasks.containsKey(id)) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = (Epic) allTasks.get(id);
        if (allTasks.containsKey(id)) {
            historyManager.add(epic);
        }
        return epic;
    }

    //Методы удаления задач  по айди:
    @Override
    public void removeTaskById(int id) {
        allTasks.remove(id);
        if (allTasks.containsKey(id)) {
            historyManager.remove(id);
        }
        taskIds.remove((Integer) id);
    }

    @Override
    public void removeSubtaskById(int id) { //при удалении подзадачи - проверяем, не обновился ли статус эпика
        Subtask subtask = (Subtask) allTasks.get(id);
        if (allTasks.containsKey(id)) {
            int epicId = subtask.getYourEpicId();
            allTasks.remove(id);
            historyManager.remove(id);
            subtaskIds.remove((Integer) id);
            Epic yourEpic = getEpicById(epicId);
            updateEpic(epicId, yourEpic, yourEpic.getTitle(), yourEpic.getDescription());
        }
    }

    @Override
    public void removeEpicById(int id) { // при удалении эпика удаляются все его подзадачи
        List<Subtask> listOfSub = subtasksOfEpic(id);
        for (Subtask subtask : listOfSub) {
            removeSubtaskById(subtask.getId());
        }
        allTasks.remove(id);
        if (allTasks.containsKey(id)) {
            historyManager.remove(id);
        }
        epicIds.remove((Integer) id);

    }

    // Методы создания задач разных типов:
    @Override
    public void createNewTask(Task task) {
        try {
            if (intersectionCheck(task)) {
                globalTaskId += 1;
                task.setId(globalTaskId);
                task.setStatus(Status.NEW);
                allTasks.put(globalTaskId, task);
                taskIds.add(globalTaskId);
                sortedTaskAndSubtasks.add(task);
            } else {
                throw new IntersectionCheckException("Ошибка добавления задачи:" +
                        " время выполнения не должно пересекаться с другиими задачами");
            }
        } catch (IntersectionCheckException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createNewSubtask(Subtask subtask, int yourEpicId) {
        // при создании новой подзадачи - обновляем статус эпика
        try {
            if (intersectionCheck(subtask)) {
                if (allTasks.containsKey(yourEpicId)) { // проверка, есть ли эпик,
                    // часть которого будет подзадача
                    globalTaskId += 1;
                    subtask.setId(globalTaskId);
                    subtask.setYourEpicId(yourEpicId);
                    subtask.setStatus(Status.NEW);
                    allTasks.put(globalTaskId, subtask);
                    subtaskIds.add(globalTaskId);
                    sortedTaskAndSubtasks.add(subtask);
                    Epic yourEpic = getEpicById(yourEpicId);
                    updateEpic(yourEpicId, yourEpic, yourEpic.getTitle(), yourEpic.getDescription());
                }
            } else {
                throw new IntersectionCheckException("Ошибка добавления подзадачи: " +
                        "время выполнения не должно пересекаться с другиими задачами");
            }
        } catch (IntersectionCheckException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createNewEpic(Epic epic) {
        globalTaskId += 1;
        epic.setId(globalTaskId);
        epic.setStatus(Status.NEW);
        allTasks.put(globalTaskId, epic);
        epicIds.add(globalTaskId);
    }

    // Метод для получения списка подзадач определенного эпика

    @Override
    public ArrayList<Subtask> subtasksOfEpic(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer id : subtaskIds) {
            Subtask subtask = (Subtask) allTasks.get(id);
            if (subtask != null && subtask.getYourEpicId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    // Методы обновления задач разного типа:

    @Override
    public void updateTask(int id, Task task, String title, String description, Status status, long duration,
                           LocalDateTime startTime) {
        if (allTasks.containsKey(id)) {
            try {
                Task possibleTask = new Task(title, description, duration, startTime);
                possibleTask.setId(id);
                if (intersectionCheck(possibleTask)) { // проверка на персечение
                    //(т.к. сравнивается только время, можно создать новый экземпляр класса Task)
                    allTasks.put(id, task);
                    sortedTaskAndSubtasks.remove(task); // удаляем старую версию задачи
                    task.setId(id);
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setStatus(status);
                    task.setDuration(duration);
                    task.setStartTime(startTime);
                    sortedTaskAndSubtasks.add(task); //добавляем новую версию задачи
                } else {
                    throw new IntersectionCheckException("Ошибка обновления задачи: " +
                            "время выполнения не должно пересекаться с другиими задачами");
                }
            } catch (IntersectionCheckException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void updateSubtask(int id, Subtask subtask, String title,
                              String description, Status status, int yourEpicId, long duration,
                              LocalDateTime startTime) {
        if (allTasks.containsKey(id)) {
            try {
                Task possibleTask = new Task(title, description, duration, startTime);
                possibleTask.setId(id);
                if (intersectionCheck(possibleTask)) { // проверка на персечение
                    //(т.к. сравнивается только время, можно создать новый экземпляр класса Task)
                    allTasks.put(id, subtask);
                    sortedTaskAndSubtasks.remove(subtask);
                    subtask.setId(id);
                    subtask.setTitle(title);
                    subtask.setDescription(description);
                    subtask.setYourEpicId(yourEpicId);
                    subtask.setStatus(status);
                    subtask.setStartTime(startTime);
                    subtask.setDuration(duration);
                    sortedTaskAndSubtasks.add(subtask);
                    Epic yourEpic = getEpicById(subtask.getYourEpicId());
                    updateEpic(subtask.getYourEpicId(), yourEpic, yourEpic.getTitle(), yourEpic.getDescription());
                } else {
                    throw new IntersectionCheckException("Ошибка обновления подзадачи: " +
                            "время выполнения не должно пересекаться с другиими задачами");
                }


            } catch (IntersectionCheckException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void updateEpic(int id, Epic epic, String title,
                           String description) {
        if (allTasks.containsKey(id)) {
            allTasks.put(id, epic);
            epic.setTitle(title);
            epic.setDescription(description);
            epic.setId(id);// возможность обновления эпика (Названия, описания), без изменения статуса
            ArrayList<Subtask> subtasks = subtasksOfEpic(id);
            int completedSubtasksCounter = 0; // счетчик, необходимый для подсчета
            long duration = 0;
            LocalDateTime startTime = null;
            if (subtasks.size() >= 1) {
                startTime = subtasks.get(0).getStartTime();
            }
            for (Subtask subtask : subtasks) {
                if (subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
                duration = duration + subtask.getDuration();
                if (subtask.getStatus().equals(Status.DONE)) {
                    completedSubtasksCounter += 1;
                } else if (subtask.getStatus().equals(Status.NEW)) {
                    completedSubtasksCounter -= 1;
                }
            }
            if (startTime != null) {
                LocalDateTime endTime = startTime.plusMinutes(duration);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
            }
        /*
        Далее - обновления статуса подзадачи. Счетчик будет равен размеру списка подзадач, только если все подзадачи
        имеют статус "DONE" - отсюда, эпик будет иметь статус "DONE". Счетчик будет равен (-1)*размер списка, если
        все подзадачи имеют статус "NEW" - эпик будет иметь статус "NEW".
         */
            if (subtasks.size() != 0) {
                if (completedSubtasksCounter == subtasks.size()) {
                    getEpicById(id).setStatus(Status.DONE);
                } else if (-1 * completedSubtasksCounter == subtasks.size()) {
                    getEpicById(id).setStatus(Status.NEW);
                } else {
                    getEpicById(id).setStatus(Status.IN_PROGRESS);
                }
            } else { // случай, возникающий, например, при удалении всех подзадач
                getEpicById(id).setStatus(Status.NEW);
            }
        }
    }

    public List<Integer> getEpicIds() {
        return epicIds;
    }

    public List<Integer> getTaskIds() {
        return taskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return allTasks;
    }

    public void setGlobalTaskId(int globalTaskId) {
        this.globalTaskId = globalTaskId;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTaskAndSubtasks);
    }

    public TreeSet<Task> getSortedTaskAndSubtasks() { //нужен в случае восстановления списка из файла
        return sortedTaskAndSubtasks;
    }

    public boolean intersectionCheck(Task task) {
        if (task.getStartTime() != null) { //в случае если нет времени старта пересечений не будет
            List<Task> tasks = getPrioritizedTasks();
            for (Task taskSorted : tasks) {
                if (taskSorted.getStartTime() != null && task.getId() != taskSorted.getId()) { // задачи без времени начала
                    // не учиитываем (1е условие), также как пересечение самой задачи с ее версией до обновления(2е усл)
                    if (task.getEndTime().isBefore(taskSorted.getEndTime())
                            && task.getEndTime().isAfter(taskSorted.getStartTime())) { //задача внутри задачи из списка
                        // или частично пересекается "слева"
                        return false;
                    } else if (task.getStartTime().isAfter(taskSorted.getStartTime()) //задача внутри задачи из списка
                            // или частично пересекается "справа"
                            && task.getStartTime().isBefore(taskSorted.getEndTime())) {
                        return false;
                    } else if (task.getStartTime().isBefore(taskSorted.getStartTime()) // задача из списка
                            // внутри новой задачи
                            && task.getEndTime().isAfter(taskSorted.getEndTime())) {
                        return false;
                    } else if (task.getStartTime().isEqual(taskSorted.getStartTime()) // задача совпадает с другой задачей
                            && task.getEndTime().isEqual(taskSorted.getEndTime())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
