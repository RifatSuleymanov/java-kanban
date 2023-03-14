package main.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task{
    private LocalDateTime endTime;
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String extraInfo) {
        super(name, extraInfo);
    }

    public ArrayList<Integer> getSubTasksID() {
        return subtasksIds;
    }

    public void setSubTasksId(ArrayList<Integer> subTasksId) {
        this.subtasksIds = subTasksId;
    }

    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        if (endTime == null) {
            return Optional.empty();
        } else {
            return Optional.of(endTime);
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getSubtasksString() {
        if (subtasksIds != null) {
            String result = "";
            for (int id : subtasksIds) {
                result = String.join(",", Integer.toString(id));
            }
            return result;
        } else {
            return " ";
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + super.getTitle() + '\'' +
                ", extraInfo='" + super.getExtraInfo() + '\'' +
                ", id=" + super.getId() +
                ", getStatus=" + super.getTaskStatus() +
                ", subTasksID=" + subtasksIds +
                ", startTime=" + super.getStartTime() +
                ", duration=" + super.getDuration() +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIds, epic.subtasksIds)&& Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds, endTime);
    }
}