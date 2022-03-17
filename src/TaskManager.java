import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    // Создаем три хранилища для задач разных типов и поле для хранения айди
    HashMap<Integer, Task> taskStorage = new HashMap<>();
    HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
    HashMap<Integer, Epic> epicStorage = new HashMap<>();
    int globalTaskId = 0;

    // Методы для получения списка задач разных типов:
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> listOfTask = new ArrayList<>();
        for (Integer key : taskStorage.keySet()) {
            listOfTask.add(taskStorage.get(key));
        }
        return listOfTask;
    }

    public ArrayList<Subtask> getSubtaskList() {
        ArrayList<Subtask> listOfSubtask = new ArrayList<>();
        for (Integer key : subtaskStorage.keySet()) {
            listOfSubtask.add(subtaskStorage.get(key));
        }
        return listOfSubtask;
    }

    public ArrayList<Epic> getEpicList() {
        ArrayList<Epic> listOfEpic = new ArrayList<>();
        for (Integer key : epicStorage.keySet()) {
            listOfEpic.add(epicStorage.get(key));
        }
        return listOfEpic;
    }

    // Методы полной очистки хранилищ задач разных типов:

    public void clearTaskStorage() {
        taskStorage.clear();
    }

    public void clearSubtaskStorage() { // при очистке списка позадач - все эпики переходят в статус "NEW"
        subtaskStorage.clear();
        for (int key : epicStorage.keySet()) {
            epicStorage.get(key).status = "NEW";
        }
    }

    public void clearEpicStorage() {
        epicStorage.clear();
    }

    // Методы получения задач по айди:

    public Task getTaskById(int id) {
        return taskStorage.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskStorage.get(id);
    }

    public Epic getEpicById(int id) {
        return epicStorage.get(id);
    }

    //Методы удаления задач  по айди:

    public void removeTaskById(int id) {
        taskStorage.remove(id);
    }

    public void removeSubtaskById(int id) { //при удалении подзадачи - проверяем, не обновился ли статус эпика
        int epicId = subtaskStorage.get(id).yourEpicId;
        subtaskStorage.remove(id);
        updateEpic(epicId, getEpicById(epicId));
    }

    public void removeEpicById(int id) { // при удалении эпика удаляются все его подзадачи
        ArrayList<Integer> idSubtasks = new ArrayList<>(); // список айди подзадач эпика
        if (epicStorage.containsKey(id)) {
            for (int yourEpicKey : subtaskStorage.keySet()) {
                if (subtaskStorage.get(yourEpicKey).yourEpicId == id) {
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

    public void createNewTask(Task task) {
        globalTaskId += 1;
        task.setId(globalTaskId);
        taskStorage.put(globalTaskId, task);
    }

    public void createNewSubtask(Subtask subtask) { // при создании новой подзадачи - обновляем статус эпика
        if (epicStorage.containsKey(subtask.yourEpicId)) { // проверка, есть ли эпик, часть которого будет подзадача
            globalTaskId += 1;
            subtask.setId(globalTaskId);
            subtaskStorage.put(globalTaskId, subtask);
            updateEpic(subtask.yourEpicId, getEpicById(subtask.yourEpicId));
        }
    }

    public void createNewEpic(Epic epic) {
        globalTaskId += 1;
        epic.setId(globalTaskId);
        epicStorage.put(globalTaskId, epic);
    }

    // Метод для получения списка подзадач определенного эпика

    public ArrayList<Subtask> subtasksOfEpic(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int key : subtaskStorage.keySet()) {
            if (subtaskStorage.get(key).yourEpicId == epicId) {
                subtasks.add(subtaskStorage.get(key));
            }
        }
        return subtasks;
    }

    // Методы обновления задач разного типа:

    public void updateTask(int id, Task task) {
        taskStorage.put(id, task);
        task.setId(id);
    }

    public void updateSubtask(int id, Subtask subtask) { //обновляем подзадачу - обновляем эпик
        subtaskStorage.put(id, subtask);
        subtask.setId(id);
        updateEpic(subtask.yourEpicId, getEpicById(subtask.yourEpicId));
    }

    public void updateEpic(int id, Epic epic) {
        epicStorage.put(id, epic); // возможность обновления эпика (Названия, описания), без изменения статуса
        ArrayList<Subtask> subtasks = subtasksOfEpic(id);
        int completedSubtasksCounter = 0; // счетчик, необходимый для подсчета
        for (Subtask subtask : subtasks) {
            if (subtask.status.equals("DONE")) {
                completedSubtasksCounter += 1;
            } else if (subtask.status.equals("NEW")) {
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
                getEpicById(id).status = "DONE";
            } else if (-1 * completedSubtasksCounter == subtasks.size()) {
                getEpicById(id).status = " NEW";
            } else {
                getEpicById(id).status = "IN_PROGRESS";
            }
        } else { // случай, возникающий, например, при удалении всех подзадач
            getEpicById(id).status = " NEW";
        }
    }
}


