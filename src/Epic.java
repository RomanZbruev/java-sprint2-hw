public class Epic extends Task {

    private int id;
    public String status;

    /*
    Для объекта Epic используется только один конструктор,
    так как после его создания статус эпика рассчитывается исходя из статусов его подзадач.
    Перезаписываются геттер/сеттер для id, так как данное поле имеет статус
    private.
     */

    public Epic(String title, String description) {
        super(title, description);
        this.status = "NEW";
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
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
