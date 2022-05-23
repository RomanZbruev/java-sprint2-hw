package model.tracker;

public class Epic extends Task {

    public Epic(String title, String description){
        super(title,description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", Id=" + getId() +
                ", status='" + getStatus().toString() + '\'' +
                '}';
    }
}
