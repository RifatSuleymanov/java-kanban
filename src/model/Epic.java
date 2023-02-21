package model;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description);
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
                "title='" + super.getName() + '\'' +
                ", extraInfo='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", taskStatus=" + super.getStatus() +
                ", subTasksID=" + subtasksIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIds, epic.subtasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }
}