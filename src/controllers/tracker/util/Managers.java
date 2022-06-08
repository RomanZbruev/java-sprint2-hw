package controllers.tracker.util;

import controllers.history.HistoryManager;
import controllers.history.InMemoryHistoryManager;
import controllers.tracker.FileBackedTasksManager;
import controllers.tracker.TaskManager;
import controllers.tracker.server.HTTPTaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileManager() {
        return new FileBackedTasksManager(new File("./src/storage/history.csv"));
    }
}
