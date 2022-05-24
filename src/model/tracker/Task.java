package model.tracker;

import controllers.tracker.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String title;
    private String description;
    private int id;
    private Status status;
    private long duration;
    private LocalDateTime startTime;

    public Task(String title, String description, long duration, LocalDateTime startTime){
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if(startTime!=null){
        return startTime.plusMinutes(duration);}
        return null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration
                && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, duration, startTime);
    }
}
