package controllers.history;

import model.tracker.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() < 10){
            historyList.add(task);
        }
        else {
            historyList.remove(0);
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

}
