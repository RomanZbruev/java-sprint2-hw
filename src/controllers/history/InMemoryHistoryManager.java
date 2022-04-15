package controllers.history;

import model.tracker.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList customLinkedList = new CustomLinkedList();

    static class CustomLinkedList {
        private Node head;
        private Node tail;
        private final HashMap<Integer,Node> nodes = new HashMap<>();

        public void linkedLast(Task task){
            final Node oldTail = tail;
            final Node newNode = new Node(task, null, oldTail);
            tail = newNode;
            if (oldTail == null){
                head = newNode;
            }
            else {
                oldTail.next = newNode;
            }
            nodes.put(task.getId(),tail);
        }

        public ArrayList<Task> getTasks(){
            ArrayList<Task> tasks = new ArrayList<>();
            if (head != null) { // если head = null - история вызовов пуста, сразу возвращаем пустой список
                Node counter = head; // заносим в переменную значение head, затем циклом проходим по всем узлам
                tasks.add(counter.data);
                while (counter.next != null) {
                    counter = counter.next;
                    tasks.add(counter.data);
                }
            }
            return tasks;
        }

        public void removeNode(Node node){
            if (nodes.containsValue(node)){
                nodes.remove(node.data.getId()); //удаляем из списка узлов
                // Далее - логика перемещения указателей
                if (node.prev == null && node.next == null){ //Случай удаления единственного узла
                    tail = null;
                    head = null;
                }
                else if (node.prev == null){ /*Крайний левый случай. Делаем головой второй элемент, удаляем ссылку
                    на первый */
                    node.next.prev = null;
                    head = node.next;
                }
                else if (node.next == null){ //крайний правый случай
                    node.prev.next = null;
                    tail = node.prev;
                }

                else { //случай "в середине"
                    node.prev.next = node.next;
                    node.next.prev = node.prev;
                }
            }
        }
    }

    @Override
    public void add(Task task) {
        if (customLinkedList.nodes.containsKey(task.getId())){
            customLinkedList.removeNode(customLinkedList.nodes.get(task.getId()));
        }
        customLinkedList.linkedLast(task);
    }

    @Override
    public void remove(int id){
        customLinkedList.removeNode(customLinkedList.nodes.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

}
