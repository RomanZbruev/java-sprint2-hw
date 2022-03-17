public class Subtask extends Task {

    public int yourEpicId;
    private int id;
    public String status;

    /*
    Для объекта предусмотрены два конструктора - первый используется при создании новой задачи,
    второй - при обновлении имеющейся задачи. Перезаписываются геттер/сеттер для id,
    так как данное поле имеет статус  private.
     */

    public Subtask(String title, String description, int yourEpicId) {
        super(title, description);
        this.yourEpicId = yourEpicId;
        this.status = "NEW";
    }

    public Subtask(String title, String description, String status, int yourEpicId) {
        super(title, description);
        this.status = status;
        this.yourEpicId = yourEpicId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + yourEpicId +
                ", subtaskId=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
