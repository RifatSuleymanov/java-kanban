package model;

import java.util.Objects;

public class Subtask extends Task{
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int epicID) {
        super(id, name, description, Status.NEW);
        this.epicId = epicID;
    }

    public int getEpicID() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" + "id=" + super.getId() +
                ", epicID=" + epicId +
                ", title='" + super.getName() + '\'' +
                ", extraInfo='" + super.getDescription() + '\'' +
                ", taskStatus=" + super.getStatus() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subTask = (Subtask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}