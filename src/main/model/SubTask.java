package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task{
    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId, LocalDateTime startTime, Duration duration){
        super(name,description, Status.NEW, startTime, duration);
        this.epicId = epicId;
    }
    public SubTask(int id, String name, String description,
                   Status subTaskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, description, subTaskStatus, startTime, duration);
        this.epicId = epicId;
    }
    public SubTask(int id, String name, String description, int epicID, LocalDateTime startTime, Duration duration) {
        super(id, name, description, startTime, duration);
        this.epicId = epicID;
    }
    public SubTask(int id, String title, String extraInfo, Status subTaskStatus, int epicId) {
        super(id, title, extraInfo, subTaskStatus);
        this.epicId = epicId;
    }

    public int getEpicID() {
        return epicId;
    }

    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
    @Override
    public String toString() {
        return "SubTask{" + "id=" + super.getId() +
                ", epicId=" + epicId +
                ", title='" + super.getTitle() + '\'' +
                ", extraInfo='" + super.getExtraInfo() + '\'' +
                ", taskStatus=" + super.getTaskStatus() +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}