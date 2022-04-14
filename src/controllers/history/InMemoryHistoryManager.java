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
        private final List<Integer> order = new ArrayList<>(); //храним порядок добавления- по Id

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
            for(Integer i : order){
                tasks.add(nodes.get(i).data);
            }
            return tasks;
        }

        public void removeNode(Node node){
            if (nodes.containsValue(node)){
                nodes.remove(node.data.getId()); //удаляем из списка узлов
                order.remove((Integer) node.data.getId()); /*здесь и далее - удаление данных о самом
                последнем обращении к задаче (очистка от дубликатов) */
                // Далее - логика перемещения указателей
                if (node.prev == null && node.next == null){ //Случай удаления единственного узла
                    tail = null;
                    head = null;
                }
                else if (node.prev == null){ /*Крайний левый случай. Делаем головой второй элемент, удаляем ссылку
                    на первый */
                    node.next.prev = null;
                    head = node;
                    nodes.put(node.data.getId(), node);
                }
                else if (node.next == null){ //крайний правый случай
                    node.prev.next = null;
                    tail = node;
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
        if (customLinkedList.nodes.containsKey(task.getId())){ /*проверка на предыдущие вызовы задачи
         и удаление вызовов*/

            customLinkedList.order.remove((Integer) task.getId());
            customLinkedList.removeNode(customLinkedList.nodes.get(task.getId()));
        }
        customLinkedList.linkedLast(task);
        customLinkedList.order.add(task.getId());

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
