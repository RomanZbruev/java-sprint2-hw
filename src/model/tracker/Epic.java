package model.tracker;

import java.time.LocalDateTime;
import java.util.Objects;

public class Epic extends Task {
    private LocalDateTime endTime;

    public Epic(String title, String description){
        this.setTitle(title);
        this.setDescription(description);
    }

    @Override
    public LocalDateTime getEndTime(){
       if (endTime!=null) return endTime;
       return null;
    }

    public void setEndTime(LocalDateTime endTime){
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", Id=" + getId() +
                ", status='" + getStatus().toString() + '\'' +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endTime);
    }
}
