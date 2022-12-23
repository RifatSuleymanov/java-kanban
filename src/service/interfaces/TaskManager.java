package service.interfaces;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtasks(Subtask subTask);

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

    void removeEpic(int id);

    void updateStatusSubTask(int id, Status subTaskStatus) throws Exception;

    HistoryManager getHistory();
}