import controllers.tracker.InMemoryTaskManager;
import controllers.tracker.Managers;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = (InMemoryTaskManager) Managers.getDefault(); // явное приведение -
        // из-за доп методов
        System.out.println("Создаем две задачи");
        Task task1 = new Task();
        taskManager.createNewTask(task1,"task1", "task1 description");
        Task task2 = new Task();
        taskManager.createNewTask(task2,"task2", "task2 description");
        System.out.println("Создаем эпик с тремя подзадачами");
        Epic epic1 = new Epic();
        taskManager.createNewEpic(epic1,"epic1", "epic1 description");
        Subtask subtask1 = new Subtask();
        taskManager.createNewSubtask(subtask1,"subtask1", "subtask1 description", epic1.getId());
        Subtask subtask2 = new Subtask();
        taskManager.createNewSubtask(subtask2,"subtask2", "subtask2 description", epic1.getId());
        Subtask subtask3 = new Subtask();
        taskManager.createNewSubtask(subtask3,"subtask3", "subtask3 description", epic1.getId());
        System.out.println("Создаем эпик без подзадач");
        Epic epic2 = new Epic();
        taskManager.createNewEpic(epic2,"epic2", "epic2 description");
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory()); //печатается эпик, так как при добавлении
        //подзадач используется метод getEpic()
        System.out.println("Распечатали списки");
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println("--------------");
        System.out.println("Запрашиваем задачи в разном порядке");
        taskManager.getTaskById(task2.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getEpicById(epic1.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubtaskById(subtask1.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getEpicById(epic2.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubtaskById(subtask3.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getSubtaskById(subtask2.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        taskManager.getTaskById(task1.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println("--------------");
        System.out.println("Удаляем задачу, смотрим историю");
        taskManager.removeTaskById(task2.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println("Удаляем эпик с тремя подзадачами, смотрим историю");
        taskManager.removeEpicById(epic1.getId());
        System.out.println("История:");
        System.out.println(taskManager.getHistoryManager().getHistory());
    }
}
