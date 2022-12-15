package model;

import java.util.Objects;

public class SubTask extends Task{
    private final int epicID; //


    public SubTask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public SubTask(int id, String name, String description, int epicID) {
        super(id, name, description, Status.NEW);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "SubTask{" + "id=" + super.getId() +
                ", epicID=" + epicID +
                ", title='" + super.getName() + '\'' +
                ", extraInfo='" + super.getDescription() + '\'' +
                ", taskStatus=" + super.getStatus() + '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicID == subTask.epicID;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }

}
