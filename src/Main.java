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
        System.out.println("Создаем эпик с двумя подзадачами");
        Epic epic1 = new Epic();
        taskManager.createNewEpic(epic1,"epic1", "epic1 description");
        Subtask subtask1 = new Subtask();
        taskManager.createNewSubtask(subtask1,"subtask1", "subtask2 description", epic1.getId());
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        /* при создании подзадачи getEpic вызывается два раза -
        когда вызываем обновление эпика (см. метод createNewSubtask) и во время обновления эпика-
        обновление его статуса (см. updateEpic) */
        Subtask subtask2 = new Subtask();
        taskManager.createNewSubtask(subtask2,"subtask2", "subtask2 description", epic1.getId());
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        System.out.println("Распечатали списки");
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println("--------------");
        System.out.println("Обновим статус обычной задачи, одной подзадачи эпика.");
        Task newTask1 = new Task();
        taskManager.updateTask(task1.getId(), newTask1, "task1new",
                "task1new desc", InMemoryTaskManager.Status.IN_PROGRESS);
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        taskManager.getTaskById(task1.getId());
        Subtask newSubtask1 = new Subtask();
        taskManager.updateSubtask(subtask1.getId(), newSubtask1, "subtask1new", "subtask1new desc",
                InMemoryTaskManager.Status.DONE, epic1.getId());
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        taskManager.getTaskById(task1.getId());
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        taskManager.getSubtaskById(subtask2.getId());
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        taskManager.getEpicById(epic1.getId());
        System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        System.out.println("-------------------- ");
        for (int i = 0; i < 5; i ++){
            taskManager.getTaskById(task2.getId());
            System.out.println("История: " +taskManager.getHistoryManager().getHistory());
        }
    }
}
