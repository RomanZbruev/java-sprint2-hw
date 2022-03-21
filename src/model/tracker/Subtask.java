package model.tracker;

import java.util.Objects;

public class Subtask extends Task {

    private int yourEpicId;

    public int getYourEpicId() {
        return yourEpicId;
    }

    public void setYourEpicId(int yourEpicId) {
        this.yourEpicId = yourEpicId;
    }

    @Override
    public String toString() {
        return "model.tracker.Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + yourEpicId +
                ", subtaskId=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }
    // перезаписываем методы equals и hashcode, так как добавляется одно дополнительное поле

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return yourEpicId == subtask.yourEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), yourEpicId);
    }
}
