package SameMethodsTests;

import controllers.tracker.TaskManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import controllers.tracker.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    public void emptyTaskListTest() {
        List<Task> expected = new ArrayList<>();
        Assertions.assertEquals(expected, taskManager.getTaskList());
    }

    @Test
    public void emptySubtaskListTest() {
        List<Subtask> expected = new ArrayList<>();
        Assertions.assertEquals(expected, taskManager.getSubtaskList());
    }

    @Test
    public void emptyEpicListTest() {
        List<Epic> expected = new ArrayList<>();
        Assertions.assertEquals(expected, taskManager.getEpicList());
    }

    @Test
    public void standardWorkTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Assertions.assertEquals(List.of(task), taskManager.getTaskList());
    }

    @Test
    public void standardWorkSubtaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic); // так как нельзя создавать подзадачи без эпика
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Assertions.assertEquals(List.of(subtask), taskManager.getSubtaskList());
    }

    @Test
    public void standardWorkEpicListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Assertions.assertEquals(List.of(epic), taskManager.getEpicList());
    }

    @Test
    public void clearEmptyTaskListTest() {
        taskManager.clearTaskStorage();
        Assertions.assertEquals(List.of(), taskManager.getTaskList());
    }

    @Test
    public void clearEmptySubtaskListTest() {
        taskManager.clearSubtaskStorage();
        Assertions.assertEquals(List.of(), taskManager.getSubtaskList());
    }

    @Test
    public void clearEmptyEpicListTest() {
        taskManager.clearEpicStorage();
        Assertions.assertEquals(List.of(), taskManager.getEpicList());
    }

    @Test
    public void standardWorkClearSubtaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task);
        taskManager.clearSubtaskStorage();
        Assertions.assertEquals(List.of(), taskManager.getTaskList());
    }

    @Test
    public void standardWorkClearTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        taskManager.clearTaskStorage();
        Assertions.assertEquals(List.of(), taskManager.getTaskList());
    }

    @Test
    public void standardWorkClearEpicListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        taskManager.clearEpicStorage();
        Assertions.assertEquals(List.of(), taskManager.getEpicList());
    }

    @Test
    public void getTaskByIdInEmptyTaskListTest() {
        Assertions.assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void getSubtaskByIdInEmptyTaskListTest() {
        Assertions.assertNull(taskManager.getSubtaskById(1));
    }

    @Test
    public void getEpicByIdInEmptyTaskListTest() {
        Assertions.assertNull(taskManager.getEpicById(1));
    }

    @Test
    public void standardWorkGetTaskByIdTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(expected, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void standardWorkGetSubtaskByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask); // айди 2
        Subtask expected = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        expected.setId(2);
        expected.setStatus(Status.NEW);
        expected.setYourEpicId(1);
        Assertions.assertEquals(expected, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void standardWorkGetEpicByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Epic expected = new Epic("t", "d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(expected, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void incorrectIdGetTaskByIdTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Assertions.assertNull(taskManager.getTaskById(task.getId() + 1));
    }

    @Test
    public void incorrectIdGetSubtaskByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask); // айди 2
        Assertions.assertNull(taskManager.getSubtaskById(subtask.getId() + 1111));
    }

    @Test
    public void incorrectIdGetEpicByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Assertions.assertNull(taskManager.getEpicById(epic.getId() + 11));
    }

    @Test
    public void removeTaskByIdInEmptyTaskListTest() {
        taskManager.removeTaskById(1);
        Assertions.assertEquals(List.of(), taskManager.getTaskList());
    }

    @Test
    public void removeSubtaskByIdInEmptyTaskListTest() {
        taskManager.removeSubtaskById(1);
        Assertions.assertEquals(List.of(), taskManager.getSubtaskList());
    }

    @Test
    public void removeEpicByIdInEmptyTaskListTest() {
        taskManager.removeEpicById(1);
        Assertions.assertEquals(List.of(), taskManager.getEpicList());
    }

    @Test
    public void standardWorkRemoveTaskByIdTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        taskManager.removeTaskById(task.getId());
        Assertions.assertEquals(List.of(), taskManager.getTaskList());
    }

    @Test
    public void standardWorkRemoveSubtaskByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask); // айди 2
        taskManager.removeSubtaskById(subtask.getId());
        Assertions.assertEquals(List.of(), taskManager.getSubtaskList());
    }

    @Test
    public void standardWorkRemoveEpicByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        taskManager.removeEpicById(epic.getId());
        Assertions.assertEquals(List.of(), taskManager.getEpicList());
    }

    @Test
    public void incorrectIdRemoveTaskByIdTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        taskManager.removeTaskById(2222);
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setStatus(Status.NEW);
        expected.setId(1);
        Assertions.assertEquals(List.of(expected), taskManager.getTaskList());
    }

    @Test
    public void incorrectIdRemoveSubtaskByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask); // айди 2
        taskManager.removeSubtaskById(232131);
        Subtask expected = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        expected.setId(2);
        expected.setStatus(Status.NEW);
        expected.setYourEpicId(1);
        Assertions.assertEquals(List.of(expected), taskManager.getSubtaskList());
    }

    @Test
    public void incorrectIdRemoveEpicByIdTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        taskManager.removeEpicById(31241285);
        Epic expected = new Epic("t", "d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected), taskManager.getEpicList());
    }

    @Test
    public void createTaskInEmptyTaskListTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected), taskManager.getTaskList());
    }

    @Test
    public void createSubtaskInEmptyTaskListTest() { // при попытке добавления подзадачи без задачи добавление
        // не будет производиться
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),1);
        taskManager.createNewSubtask(subtask);
        Assertions.assertEquals(List.of(), taskManager.getSubtaskList());
    }

    @Test
    public void createEpicInEmptyTaskListTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Epic expected = new Epic("t", "d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected), taskManager.getEpicList());
    }

    @Test
    public void standardWorkCreateTaskTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Task task1 = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 10));

        taskManager.createNewTask(task1);
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setStatus(Status.NEW);
        expected.setId(1);
        Task expected1 = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 10));
        expected1.setStatus(Status.NEW);
        expected1.setId(2);
        Assertions.assertEquals(List.of(expected, expected1), taskManager.getTaskList());
    }

    @Test
    public void standardWorkCreateSubtaskTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Subtask expected = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        expected.setId(2);
        expected.setStatus(Status.NEW);
        expected.setYourEpicId(1);
        Assertions.assertEquals(List.of(expected), taskManager.getSubtaskList());
    }

    @Test
    public void standardWorkCreateEpicTest() {
        Epic task = new Epic("t", "d");
        taskManager.createNewEpic(task);
        Epic task1 = new Epic("t", "d");
        taskManager.createNewEpic(task1);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.NEW);
        expected.setId(1);
        Epic expected1 = new Epic("t", "d");
        expected1.setStatus(Status.NEW);
        expected1.setId(2);
        Assertions.assertEquals(List.of(expected, expected1), taskManager.getEpicList());
    }

    @Test
    public void subtasksOfEpicWithEmptyListTest() {
        Assertions.assertEquals(List.of(), taskManager.subtasksOfEpic(1));
    }

    @Test
    public void standardWorkSubtasksOfEpic() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Subtask expected = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        expected.setStatus(Status.NEW);
        expected.setId(2);
        expected.setYourEpicId(1);
        Assertions.assertEquals(List.of(expected), taskManager.subtasksOfEpic(epic.getId()));
    }

    @Test
    public void incorrectIdWorkSubtasksOfEpic() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Assertions.assertEquals(List.of(), taskManager.subtasksOfEpic(82828));
    }

    @Test
    public void epicToSubtaskLinkTest() { // проверяем наличие эпика для подзадачи -> возможность получить по
        // закрепленному в подзадаче айди эпика - исходный эпик
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(subtask);
        Assertions.assertEquals(epic, taskManager.getEpicById(subtask.getYourEpicId()));
    }

    @Test
    public void updateTaskInEmptyTaskListTest() {  // нет задач - нечего обновлять
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        task.setId(1);
        taskManager.updateTask(task);
        Assertions.assertEquals(List.of(), taskManager.getTaskList());
    }

    @Test
    public void updateSubtaskInEmptyTaskListTest() {  // нет задач - нечего обновлять
        Subtask subtask = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),1);
        subtask.setId(2);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(List.of(), taskManager.getSubtaskList());
    }

    @Test
    public void updateEpicInEmptyTaskListTest() {  // нет задач - нечего обновлять
        Epic epic = new Epic("t", "d");
        epic.setId(1);
        taskManager.updateEpic(epic);
        Assertions.assertEquals(List.of(), taskManager.getEpicList());
    }

    @Test
    public void standardWorkUpdateTaskTest() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Task updatedTask = new Task(task.getTitle(), task.getDescription(),1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        updatedTask.setId(1);
        updatedTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setStatus(Status.IN_PROGRESS);
        expected.setId(1);
        Assertions.assertEquals(expected, taskManager.getTaskById(task.getId()));
    }

    @Test
    public void standardWorkUpdateSubtaskTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription(),1,
                LocalDateTime.of(2000, 1, 1, 1, 1), task.getYourEpicId());
        updatedSubtask.setId(2);
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(updatedSubtask);
        Subtask expected = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),1);
        expected.setStatus(Status.IN_PROGRESS);
        expected.setId(2);
        Assertions.assertEquals(expected, taskManager.getSubtaskById(task.getId()));
    }

    @Test
    public void standardWorkUpdateEpicTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Epic updatedEpic = new Epic("tNEW", "dNEW");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);
        Epic expected = new Epic("tNEW", "dNEW");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(expected, taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void incorrectIdUpdateTask() {
        Task task = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        taskManager.createNewTask(task);
        Task updatedTask = new Task(task.getTitle(), task.getDescription(),1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        updatedTask.setStatus(Status.IN_PROGRESS);
        updatedTask.setId(2323);
        taskManager.updateTask(updatedTask);
        Task expected = new Task("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setStatus(Status.NEW);
        expected.setId(1);
        Assertions.assertEquals(expected, task);
    }

    @Test
    public void incorrectIdUpdateSubtaskTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription(),1,
                LocalDateTime.of(2000, 1, 1, 1, 1), task.getYourEpicId());
        updatedSubtask.setId(53535);
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(updatedSubtask);
        Subtask expected = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        expected.setStatus(Status.NEW);
        expected.setId(2);
        expected.setYourEpicId(1);
        Assertions.assertEquals(expected, task);
    }

    @Test
    public void incorrectWorkUpdateEpicTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Epic updatedEpic = new Epic("tNEW", "dNEW");
        updatedEpic.setId(1312312);
        taskManager.updateEpic(updatedEpic);
        Epic expected = new Epic("t", "d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusOneInProgressSubtaskTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        updatedSubtask.setId(2);
        taskManager.updateSubtask(updatedSubtask);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.IN_PROGRESS);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(1);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 2));
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusOneDoneSubtaskTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(2);
        taskManager.updateSubtask(updatedSubtask);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.DONE);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(1);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 2));
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusTwoSubtasksNewAndInProgressTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task1 = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task1);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        updatedSubtask.setId(3);
        taskManager.updateSubtask(updatedSubtask);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.IN_PROGRESS);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(2);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 3));
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusTwoSubtasksNewAndDoneTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task1 = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1), epic.getId());
        taskManager.createNewSubtask(task1);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 2), task1.getYourEpicId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(3);
        taskManager.updateSubtask(updatedSubtask);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.IN_PROGRESS);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(2);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 3));
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusTwoSubtasksBothInProgressTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task1 = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1), epic.getId());
        taskManager.createNewSubtask(task1);
        Subtask updatedSubtask1 = new Subtask(task1.getTitle(), task1.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask1.setStatus(Status.IN_PROGRESS);
        updatedSubtask1.setId(2);
        taskManager.updateSubtask(updatedSubtask1);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 2), task1.getYourEpicId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        updatedSubtask.setStatus(Status.IN_PROGRESS);
        updatedSubtask.setId(3);
        taskManager.updateSubtask(updatedSubtask);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.IN_PROGRESS);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(2);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 3));
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusTwoSubtasksBothDoneTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task1 = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task1);
        Subtask updatedSubtask1 = new Subtask(task1.getTitle(), task1.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask1.setStatus(Status.DONE);
        updatedSubtask1.setId(2);
        taskManager.updateSubtask(updatedSubtask1);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(3);
        taskManager.updateSubtask(updatedSubtask);
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.DONE);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(2);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 3));
        Assertions.assertEquals(expected, epic);
    }

    @Test
    public void updateEpicStatusTwoSubtasksBothDoneAndClearTest() {
        Epic epic = new Epic("t", "d");
        taskManager.createNewEpic(epic);
        Subtask task1 = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        taskManager.createNewSubtask(task1);
        Subtask updatedSubtask1 = new Subtask(task1.getTitle(), task1.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 1),epic.getId());
        updatedSubtask1.setStatus(Status.DONE);
        updatedSubtask1.setId(2);
        taskManager.updateSubtask(updatedSubtask1);
        Subtask task = new Subtask("t", "d", 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        taskManager.createNewSubtask(task);
        Subtask updatedSubtask = new Subtask(task.getTitle(), task.getDescription()
                , 1,
                LocalDateTime.of(2000, 1, 1, 1, 2),epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(3);
        taskManager.updateSubtask(updatedSubtask);
        taskManager.clearSubtaskStorage();
        Epic expected = new Epic("t", "d");
        expected.setStatus(Status.NEW);
        expected.setId(1);
        expected.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 1));
        expected.setDuration(2);
        expected.setEndTime(LocalDateTime.of(2000, 1, 1, 1, 3));
        Assertions.assertEquals(expected, epic);
    }
}

