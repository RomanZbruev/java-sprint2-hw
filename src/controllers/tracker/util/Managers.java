package controllers.tracker.util;

import controllers.history.HistoryManager;
import controllers.history.InMemoryHistoryManager;
import controllers.tracker.FileBackedTasksManager;
import controllers.tracker.InMemoryTaskManager;
import controllers.tracker.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileManager() {
        return new FileBackedTasksManager(new File("./src/storage/history.csv"));
    }
}
