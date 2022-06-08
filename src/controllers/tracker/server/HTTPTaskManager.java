package controllers.tracker.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.tracker.FileBackedTasksManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HTTPTaskManager extends FileBackedTasksManager {

    KVTaskClient taskClient;
    private static final Gson gson = new Gson();
    public HTTPTaskManager(String url) {
        super(new File("./src/storage/historyTest.csv"));
        taskClient = new KVTaskClient(url);
        load();
    }

    public void load() {
        HashMap<Integer, Task> tasks = gson.fromJson(taskClient.load("tasks"),
                new TypeToken<HashMap<Integer, Task>>() {
                }.getType());
        HashMap<Integer, Subtask> subtasks = gson.fromJson(taskClient.load("subtasks"),
                new TypeToken<HashMap<Integer, Subtask>>() {
                }.getType());
        HashMap<Integer, Epic> epics = gson.fromJson(taskClient.load("epics"),
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());
        List<Integer> history = gson.fromJson(taskClient.load("history"),
                new TypeToken<List<Integer>>(){}.getType());
        if (!taskClient.load("globalId").isBlank()) {
            int id = Integer.parseInt(taskClient.load("globalId"));
            setGlobalTaskId(id);
        }
        if (tasks != null) {
            loadTask(tasks);
        }
        if (epics != null) {
            loadEpics(epics);
        }
        if (subtasks != null) {
            loadSubtask(subtasks);
        }
        if (history != null) {
            loadHistory(history);
        }
    }

    public void loadTask(HashMap<Integer, Task> tasks) {
        for (Integer id : tasks.keySet()) {
            getTasks().put(id, tasks.get(id));
        }
    }


    public void loadSubtask(HashMap<Integer, Subtask> subtasks) {
        for (Integer id : subtasks.keySet()) {
            getSubtasks().put(id, subtasks.get(id));
        }
    }


    public void loadEpics(HashMap<Integer, Epic> epics) {
        for (Integer id : epics.keySet()) {
            getEpics().put(id, epics.get(id));
        }
    }

    public void loadHistory(List<Integer> idHistory) {
        for (int id : idHistory) {
            if (getTasks().containsKey(id)) {
                getTaskById(id);
            } else if (getSubtasks().containsKey(id)) {
                getSubtaskById(id);
            } else {
                getEpicById(id);
            }
        }
    }

    @Override
    public void save() {
        HashMap<Integer, Task> tasks = this.getTasks();
        HashMap<Integer, Subtask> subtasks = this.getSubtasks();
        HashMap<Integer, Epic> epics = this.getEpics();
        List<Task> history = this.getHistoryManager().getHistory();
        List<Integer> idHistory = new ArrayList<>();
        for(Task task : history){
            idHistory.add(task.getId());
        }
        taskClient.put("tasks", gson.toJson(tasks));
        taskClient.put("subtasks", gson.toJson(subtasks));
        taskClient.put("epics", gson.toJson(epics));
        taskClient.put("history", gson.toJson(idHistory));
        taskClient.put("globalId", gson.toJson(getGlobalTaskId()));
    }
}
