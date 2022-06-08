package model.tracker;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private int yourEpicId;
    public Subtask(String title, String description,long duration, LocalDateTime startTime,int yourEpicId){
        super(title,description,duration,startTime);
        this.yourEpicId = yourEpicId;
    }

    public int getYourEpicId() {
        return yourEpicId;
    }

    public void setYourEpicId(int yourEpicId) {
        this.yourEpicId = yourEpicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", yourEpicId=" + yourEpicId +
                ", Id=" + getId() +
                ", status='" + getStatus().toString() + '\'' +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
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
