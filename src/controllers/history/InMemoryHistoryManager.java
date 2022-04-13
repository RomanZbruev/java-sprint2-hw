package controllers.history;

import model.tracker.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Node> customLinkedList = new LinkedList<>();
    private final HashMap<Integer,Node> nodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())){
            removeNode(nodes.get(task.getId()));
        }
            linkLast(task);
            nodes.put(task.getId(),tail);

    }

    public void linkLast(Task task){
        final Node oldTail = tail;
        final Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        if (oldTail == null){
            head = newNode;
        }
        else {
            oldTail.next = newNode;
        }
        customLinkedList.add(newNode);
    }

    public ArrayList<Task> getTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node node : customLinkedList){
            tasks.add(node.data);
        }
        return tasks;
    }

    public void removeNode(Node node){
        customLinkedList.remove(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id){
        removeNode(nodes.get(id));
    }

}
