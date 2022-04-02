package controllers.tracker;

import controllers.history.HistoryManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    // Создаем три хранилища для задач разных типов и поле для хранения айди
    HashMap<Integer, Task> taskStorage = new HashMap<>();
    HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
    HashMap<Integer, Epic> epicStorage = new HashMap<>();
    int globalTaskId = 0;
    HistoryManager historyManager = Managers.getDefaultHistory();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    // Методы для получения списка задач разных типов:
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> listOfTask = new ArrayList<>();
        for (Integer key : taskStorage.keySet()) {
            listOfTask.add(taskStorage.get(key));
        }
        return listOfTask;
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        for (Integer key : subtaskStorage.keySet()) {
            listOfSubtask.add(subtaskStorage.get(key));
        }
        return listOfSubtask;
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> listOfEpic = new ArrayList<>();
        for (Integer key : epicStorage.keySet()) {
            listOfEpic.add(epicStorage.get(key));
        }
        return listOfEpic;
    }

    // Методы полной очистки хранилищ задач разных типов:
    @Override
    public void clearTaskStorage() {
        taskStorage.clear();
    }

    @Override
    public void clearSubtaskStorage() { // при очистке списка позадач - все эпики переходят в статус "NEW"
        subtaskStorage.clear();
        for (int key : epicStorage.keySet()) {
            epicStorage.get(key).setStatus(Status.NEW);
        }
    }

    @Override
    public void clearEpicStorage() {
        epicStorage.clear();
    }

    // Методы получения задач по айди:
    @Override
    public Task getTaskById(int id) {
        historyManager.add(taskStorage.get(id));
        return taskStorage.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtaskStorage.get(id));
        return subtaskStorage.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicStorage.get(id));
        return epicStorage.get(id);
    }

    //Методы удаления задач  по айди:
    @Override
    public void removeTaskById(int id) {
        taskStorage.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) { //при удалении подзадачи - проверяем, не обновился ли статус эпика
        int epicId = subtaskStorage.get(id).getYourEpicId();
        subtaskStorage.remove(id);
        Epic yourEpic = getEpicById(epicId);
        updateEpic(epicId, yourEpic, yourEpic.getTitle(), yourEpic.getDescription());
    }

    @Override
    public void removeEpicById(int id) { // при удалении эпика удаляются все его подзадачи
        ArrayList<Integer> idSubtasks = new ArrayList<>(); // список айди подзадач эпика
        if (epicStorage.containsKey(id)) {
            for (int yourEpicKey : subtaskStorage.keySet()) {
                if (subtaskStorage.get(yourEpicKey).getYourEpicId() == id) {
                    idSubtasks.add(subtaskStorage.get(yourEpicKey).getId()); //добавляем айди в список
                }
            }
            for (int idSub : idSubtasks) { // проходимся по списку, удаляем подзадачи
                removeSubtaskById(idSub);
            }
            epicStorage.remove(id);
        }
    }

    // Методы создания задач разных типов:
    @Override
    public void createNewTask(Task task, String title, String description) {
        globalTaskId += 1;
        task.setId(globalTaskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(Status.NEW);
        taskStorage.put(globalTaskId, task);
    }

    @Override
    public void createNewSubtask(Subtask subtask, String title, String description, int yourEpicId) {
        // при создании новой подзадачи - обновляем статус эпика
        if (epicStorage.containsKey(yourEpicId)) { // проверка, есть ли эпик,
            // часть которого будет подзадача
            globalTaskId += 1;
            subtask.setId(globalTaskId);
            subtask.setTitle(title);
            subtask.setDescription(description);
            subtask.setYourEpicId(yourEpicId);
            subtask.setStatus(Status.NEW);
            subtaskStorage.put(globalTaskId, subtask);
            Epic yourEpic = getEpicById(yourEpicId);
            updateEpic(yourEpicId, yourEpic, yourEpic.getTitle(), yourEpic.getDescription());
        }
    }

    @Override
    public void createNewEpic(Epic epic, String title, String description) {
        globalTaskId += 1;
        epic.setId(globalTaskId);
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setStatus(Status.NEW);
        epicStorage.put(globalTaskId, epic);
    }

    // Метод для получения списка подзадач определенного эпика

    @Override
    public ArrayList<Subtask> subtasksOfEpic(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int key : subtaskStorage.keySet()) {
            if (subtaskStorage.get(key).getYourEpicId() == epicId) {
                subtasks.add(subtaskStorage.get(key));
            }
        }
        return subtasks;
    }

    // Методы обновления задач разного типа:

    @Override
    public void updateTask(int id, Task task, String title, String description, Status status) {
        taskStorage.put(id, task);
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
    }

    @Override
    public void updateSubtask(int id, Subtask subtask, String title,
                              String description, Status status, int yourEpicId) {
        //обновляем подзадачу - обновляем эпик
        subtaskStorage.put(id, subtask);
        subtask.setId(id);
        subtask.setTitle(title);
        subtask.setDescription(description);
        subtask.setYourEpicId(yourEpicId);
        subtask.setStatus(status);
        Epic yourEpic = getEpicById(subtask.getYourEpicId());
        updateEpic(subtask.getYourEpicId(), yourEpic, yourEpic.getTitle(), yourEpic.getDescription());
    }

    @Override
    public void updateEpic(int id, Epic epic, String title,
                           String description) {
        epicStorage.put(id, epic);
        epic.setTitle(title);
        epic.setDescription(description);
        epic.setId(id);// возможность обновления эпика (Названия, описания), без изменения статуса
        ArrayList<Subtask> subtasks = subtasksOfEpic(id);
        int completedSubtasksCounter = 0; // счетчик, необходимый для подсчета
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals(Status.DONE)) {
                completedSubtasksCounter += 1;
            } else if (subtask.getStatus().equals(Status.NEW)) {
                completedSubtasksCounter -= 1;
            }
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
