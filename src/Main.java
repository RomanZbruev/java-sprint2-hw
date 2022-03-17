
public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        System.out.println("Создаем две задачи");
        Task task1 = new Task("task1", "task1 description");
        taskManager.createNewTask(task1);
        Task task2 = new Task("task2", "task2 description");
        taskManager.createNewTask(task2);
        System.out.println("Создаем эпик с двумя подзадачами");
        Epic epic1 = new Epic("epic1", "epic1 description");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "subtask2 description", epic1.getId());
        taskManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "subtask2 description", epic1.getId());
        taskManager.createNewSubtask(subtask2);
        System.out.println("Создаем эпик с одной подзадачей");
        Epic epic2 = new Epic("epic2", "epic2 description");
        taskManager.createNewEpic(epic2);
        Subtask subtask3 = new Subtask("subtask3", "subtask3 description", epic2.getId());
        taskManager.createNewSubtask(subtask3);
        System.out.println("Распечатали списки");
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println("--------------");
        System.out.println("Обновим статус обычной задачи, по одной подзадачи эпиков.");
        Task newTask1 = new Task("task1new", "task1new desc", "IN_PROGRESS");
        taskManager.updateTask(task1.getId(), newTask1);
        Subtask newSubtask1 =
                new Subtask("subtask1new", "subtask1new  desc", "DONE", subtask1.yourEpicId);
        taskManager.updateSubtask(subtask1.getId(), newSubtask1);
        Subtask newSubtask3 =
                new Subtask("subtask3new", "subtask3new  desc", "DONE", subtask3.yourEpicId);
        taskManager.updateSubtask(subtask3.getId(), newSubtask3);
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println("--------------");
        System.out.println("Удаляем задачу и эпик (по задумке, с эпиком удаляются и его подзадачи) " +
                "и попробуем удалить задачу с неправильным айди");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic2.getId());
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println("--------------");
        taskManager.removeTaskById(-10);
        System.out.println(taskManager.getTaskList());
        System.out.println("--------------");
        System.out.println("Проверка получения задач по айди");
        System.out.println(taskManager.getTaskById(task2.getId()));
        System.out.println(taskManager.getSubtaskById(subtask1.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println("--------------");
        System.out.println("Проверка очистки хранилищ задач");
        taskManager.clearEpicStorage();
        taskManager.clearSubtaskStorage();
        taskManager.clearTaskStorage();
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getSubtaskList());
        System.out.println(taskManager.getEpicList());
    }
}
