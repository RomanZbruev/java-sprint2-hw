package controllers.tracker;

import controllers.IntersectionCheckException;
import controllers.history.HistoryManager;
import controllers.tracker.util.Managers;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    // Создаем три хранилища для задач разных типов и поле для хранения айди
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
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
        for (Integer id : tasks.keySet()) {
            listOfTask.add(tasks.get(id));
        }
        return listOfTask;
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            listOfSubtask.add(subtasks.get(id));
        }
        return listOfSubtask;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> listOfEpic = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            listOfEpic.add(epics.get(id));
        }
        return listOfEpic;
    }

    // Методы полной очистки хранилищ задач разных типов:
    @Override
    public void clearTaskStorage() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void clearSubtaskStorage() { // при очистке списка позадач - все эпики переходят в статус "NEW"
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            epics.get(id).setStatus(Status.NEW);
        }
    }

    @Override
    public void clearEpicStorage() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    // Методы получения задач по айди:
    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    //Методы удаления задач  по айди:
    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) { //при удалении подзадачи - проверяем, не обновился ли статус эпика
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getYourEpicId();
            subtasks.remove(id);
            historyManager.remove(id);
            Epic yourEpic = getEpicById(epicId);
            updateEpic(yourEpic);
        }
    }

    @Override
    public void removeEpicById(int id) { // при удалении эпика удаляются все его подзадачи
        if (epics.containsKey(id)) {
            List<Subtask> listOfSub = subtasksOfEpic(id);
            for (Subtask subtask : listOfSub) {
                removeSubtaskById(subtask.getId());
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    // Методы создания задач разных типов:
    @Override
    public void createNewTask(Task task) {
        try {
            if (intersectionCheck(task)) {
                globalTaskId += 1;
                task.setId(globalTaskId);
                task.setStatus(Status.NEW);
                tasks.put(globalTaskId, task);
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
    public void createNewSubtask(Subtask subtask) {
        // при создании новой подзадачи - обновляем статус эпика
        try {
            if (intersectionCheck(subtask)) {
                if (epics.containsKey(subtask.getYourEpicId())) { // проверка, есть ли эпик,
                    // часть которого будет подзадача
                    globalTaskId += 1;
                    subtask.setId(globalTaskId);
                    subtask.setStatus(Status.NEW);
                    subtasks.put(globalTaskId, subtask);
                    sortedTaskAndSubtasks.add(subtask);
                    Epic yourEpic = getEpicById(subtask.getYourEpicId());
                    updateEpic(yourEpic);
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
        epics.put(globalTaskId, epic);
    }

    // Метод для получения списка подзадач определенного эпика

    @Override
    public ArrayList<Subtask> subtasksOfEpic(int epicId) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null && subtask.getYourEpicId() == epicId) {
                subtasksOfEpic.add(subtask);
            }
        }
        return subtasksOfEpic;
    }

    // Методы обновления задач разного типа:

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            try {
                if (intersectionCheck(task)) { // проверка на персечение
                    sortedTaskAndSubtasks.remove(getTaskById(task.getId())); // удаляем старую версию задачи
                    tasks.put(task.getId(), task);
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
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            try {
                if (intersectionCheck(subtask)) { // проверка на персечение
                    sortedTaskAndSubtasks.remove(getSubtaskById(subtask.getId()));
                    subtasks.put(subtask.getId(), subtask);
                    sortedTaskAndSubtasks.add(subtask);
                    Epic yourEpic = getEpicById(subtask.getYourEpicId());
                    updateEpic(yourEpic);
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
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            ArrayList<Subtask> subtasks = subtasksOfEpic(epic.getId());
            epics.put(epic.getId(), epic);
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
                getEpicById(epic.getId()).setStartTime(startTime);
                getEpicById(epic.getId()).setDuration(duration);
                getEpicById(epic.getId()).setEndTime(endTime);
            }
        /*
        Далее - обновления статуса подзадачи. Счетчик будет равен размеру списка подзадач, только если все подзадачи
        имеют статус "DONE" - отсюда, эпик будет иметь статус "DONE". Счетчик будет равен (-1)*размер списка, если
        все подзадачи имеют статус "NEW" - эпик будет иметь статус "NEW".
         */
            if (subtasks.size() != 0) {
                if (completedSubtasksCounter == subtasks.size()) {
                    getEpicById(epic.getId()).setStatus(Status.DONE);
                } else if (-1 * completedSubtasksCounter == subtasks.size()) {
                    getEpicById(epic.getId()).setStatus(Status.NEW);
                } else {
                    getEpicById(epic.getId()).setStatus(Status.IN_PROGRESS);
                }
            } else { // случай, возникающий, например, при удалении всех подзадач
                getEpicById(epic.getId()).setStatus(Status.NEW);
            }
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
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

    public int getGlobalTaskId() {
        return globalTaskId;
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
