package model;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private ArrayList<Integer> subtasksID = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description);
    }
    public ArrayList<Integer> getSubTasksID() {
        return subtasksID;
    }
    public void setSubTasksID(ArrayList<Integer> subTasksID) {
        this.subtasksID = subTasksID;
    }
    @Override
    public String toString() {
        return "Epic{" +
                "title='" + super.getName() + '\'' +
                ", extraInfo='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", taskStatus=" + super.getStatus() +
                ", subTasksID=" + subtasksID +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksID, epic.subtasksID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksID);
    }


}
