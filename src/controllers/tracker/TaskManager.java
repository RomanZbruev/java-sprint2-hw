package controllers.tracker;

import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getTaskList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Epic> getEpicList();

    void clearTaskStorage();

    void clearSubtaskStorage();

    void clearEpicStorage();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void createNewTask(Task task, String title, String description);

    void createNewSubtask(Subtask subtask, String title, String description, int yourEpicId);

    void createNewEpic(Epic epic, String title, String description);

    ArrayList<Subtask> subtasksOfEpic(int epicId);

    void updateTask(int id, Task task, String title, String description, InMemoryTaskManager.Status status);

    void updateSubtask(int id, Subtask subtask, String title,
                       String description, InMemoryTaskManager.Status status, int yourEpicId);

    void updateEpic(int id, Epic epic, String title,
                    String description);
}





