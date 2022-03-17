public class Task {

    public String title;
    public String description;
    private int id;
    public String status;

    /*
    Для объекта предусмотрены два конструктора - первый используется при создании новой задачи,
    второй - при обновлении имеющейся задачи.
     */

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = "NEW";
    }

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", taskID=" + id +
                ", status='" + status + '\'' +
                '}';
    }

}
