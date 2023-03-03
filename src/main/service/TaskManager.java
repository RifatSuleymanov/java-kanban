package main.service;

import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtasks(Subtask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubTask(int id);

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubTasks();

    void clearTasks();

    void clearEpics();

    void clearSubTasks();

    ArrayList<Integer> getSubTaskList(int epicId);

    void updateStatusEpic(int id);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    void updateStatusSubTask(int id, Status subTaskStatus);

    HistoryManager getHistory();
    List<Task> getPrioritizedTasks();

}