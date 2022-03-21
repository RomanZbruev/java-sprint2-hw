package model.tracker;

public class Epic extends Task {

    @Override
    public String toString() {
        return "model.tracker.Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
