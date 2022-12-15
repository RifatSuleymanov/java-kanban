package controller;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
public class TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();


    public void createTask(Task task){
        task.setId(counter);
        tasks.put(task.getId(), task);
        counter++;
    }


    public void createEpic(Epic epic){
        epic.setId(counter);
        epics.put(epic.getId(), epic);
        counter++;
    }


    public void createSubtasks(SubTask subTask){
        if(!epics.containsKey(subTask.getEpicID())){
            throw new RuntimeException("Ошибка");
        }
        subTask.setId(counter);
        subtasks.put(subTask.getId(), subTask);
        counter++;
        getSubTaskList(subTask.getEpicID()).add(subTask.getId());
        updateStatusEpic(subTask.getEpicID());
    }
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            return tasks.get(id);
        } else {
            return null;
        }
    }

    public Epic getEpic(int id) {
        if (epics.get(id) != null) {
            return epics.get(id);
        } else {
            return null;
        }
    }
    public SubTask getSubTask(int id) {
        if (subtasks.get(id) != null) {
            return subtasks.get(id);
        } else {
            return null;
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return new HashMap<>(subtasks);
    }

    public void clearTasks() {
        tasks.clear();
    }
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }
    public void clearSubTasks() {
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            epics.get(id).setSubTasksID(null);
            updateStatusEpic(id);
        }
    }
    public ArrayList<Integer> getSubTaskList(int epicID) {
        if (epics.get(epicID) != null) {
            return epics.get(epicID).getSubTasksID();
        } else {
            return null;
        }
    }
    public void updateStatusEpic(int id) {
        Epic epic = new Epic(id, epics.get(id).getName(),
                epics.get(id).getDescription());
        epic.setSubTasksID(epics.get(id).getSubTasksID());

        int statusNew = 0; //
        int statusInProgress = 0;
        int statusDone = 0;
        if (epic.getSubTasksID() != null) {
            for (int i = 0; i < epic.getSubTasksID().size(); i++) {
                if (subtasks.get(epic.getSubTasksID().get(i)) != null) {
                    if (subtasks.get(epic.getSubTasksID().get(i)).getStatus() == Status.NEW) {
                        statusNew++;
                    } else if (subtasks.get(epic.getSubTasksID().get(i)).getStatus() == Status.IN_PROGRESS) {
                        statusInProgress++;
                    } else {
                        statusDone++;
                    }
                }
            }

            if (statusInProgress == 0 && statusDone == 0) {
                epic.setStatus(Status.NEW);
            } else if (statusDone > 0 || (statusNew < 1 && statusInProgress < 1)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        } else {
            epic.setStatus(Status.NEW);
        }

        epics.put(epic.getId(), epic);
    }
    public void removeTask(int id) {
        tasks.remove(id);
    }
    public void removeEpic(int id) {
        if (getSubTaskList(id) != null) {
            for (int subTaskID : getSubTaskList(id)) {
                subtasks.remove(subTaskID);
            }
        }
        epics.remove(id);
    }
    public void updateStatusSubTask(int id, Status subTaskStatus) {
        SubTask subTask = new SubTask(id, subtasks.get(id).getName(),
                subtasks.get(id).getDescription(),
                subtasks.get(id).getEpicID());
        subTask.setStatus(subTaskStatus);

        subtasks.put(subTask.getId(), subTask);
        updateStatusEpic(subTask.getEpicID());
    }



}



