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

    void createNewTask (Task task);

    void createNewSubtask(Subtask subtask);

    void createNewEpic(Epic epic);

    ArrayList<Subtask> subtasksOfEpic(int epicId);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);
}





