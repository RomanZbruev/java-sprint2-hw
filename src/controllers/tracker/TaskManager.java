package controllers.tracker;

import controllers.IntersectionCheckException;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.time.LocalDateTime;
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

    void createNewTask (Task task);

    void createNewSubtask(Subtask subtask, int yourEpicId);

    void createNewEpic(Epic epic);

    ArrayList<Subtask> subtasksOfEpic(int epicId);

    void updateTask(int id, Task task, String title, String description, Status status,
                    long duration, LocalDateTime startTime);

    void updateSubtask(int id, Subtask subtask, String title,
                       String description, Status status, int yourEpicId,long duration, LocalDateTime startTime);

    void updateEpic(int id, Epic epic, String title,
                    String description);
}





