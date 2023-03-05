package main.service.impl;

import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;
import main.service.HistoryManager;
import main.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();

    Comparator<Task> taskByStartTimeComparator = (task1, task2) -> {
        int result = 0;
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            result = task2.getId() - task1.getId();
        } else if (task1.getStartTime() != null && task2.getStartTime() == null) {
            result = -1;
        } else if (task1.getStartTime() == null && task2.getStartTime() != null) {
            result = 1;
        } else if (task1.getStartTime() != null && task2.getStartTime() != null) {
            if (task1.getStartTime().isAfter(task2.getStartTime())) {
                result = 1;
            } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
                result = -1;
            }
        }
        return result;
    };

    private final TreeSet<Task> listOfTasksSortedByTime = new TreeSet<>(taskByStartTimeComparator);

    @Override
    public int createTask(Task task){
        task.setId(counter++);
        tasks.put(task.getId(), task);
        compareTasksByTimeAndAddToTreeSet(task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic){
        epic.setId(counter++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubtasks(Subtask subTask){
        if(!epics.containsKey(subTask.getEpicID())){
            throw new RuntimeException("Ошибка");
        }
        subTask.setId(counter++);
        subtasks.put(subTask.getId(), subTask);
        getSubTaskList(subTask.getEpicID()).add(subTask.getId());
        updateStatusEpic(subTask.getEpicID());
        setStartTimeForEpic(subTask.getEpicID());
        setEndTimeForEpic(subTask.getEpicID());
        setDurationForEpic(subTask.getEpicID());

        compareTasksByTimeAndAddToTreeSet(subTask);
        return subTask.getId();
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) {
            history.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new RuntimeException("Ошибка: нет таска с таким id!");
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null) {
            history.add(epics.get(id));
            return epics.get(id);
        } else {
            throw new RuntimeException("Ошибка: нет эпика с таким id!");
        }
    }

    @Override
    public Subtask getSubTask(int id) {
        if (subtasks.get(id) != null) {
            history.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new RuntimeException("Ошибка: нет сабтаска с таким id!");
        }
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    @Override
    public HashMap<Integer, Subtask> getSubTasks() {
        return new HashMap<>(subtasks);
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            listOfTasksSortedByTime.remove(task);
        }
        for (Integer id : tasks.keySet()) {
            history.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Subtask subTask : subtasks.values()) {
            listOfTasksSortedByTime.remove(subTask);
        }
        for (Integer id : subtasks.keySet()) {
            history.remove(id);
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            listOfTasksSortedByTime.remove(epic);
        }

        for (Integer id : epics.keySet()) {
            history.remove(id);
        }
        epics.clear();
    }

    @Override
    public void clearSubTasks() {

        for (Subtask subTask : subtasks.values()) {
            listOfTasksSortedByTime.remove(subTask);
        }

        for (Integer id : subtasks.keySet()) {
            history.remove(id);
        }
        subtasks.clear();

        for (Integer id : epics.keySet()) {
            getEpicWithoutHistorySaving(id).setSubTasksId(new ArrayList<>());
            getEpicWithoutHistorySaving(id).setStartTime(null);
            getEpicWithoutHistorySaving(id).setEndTime(null);
            getEpicWithoutHistorySaving(id).setDuration(null);
            updateStatusEpic(id);
        }
    }

    @Override
    public ArrayList<Integer> getSubTaskList(int epicId) {
        if (epics.get(epicId) != null) {
            return (ArrayList<Integer>) epics.get(epicId).getSubTasksID();
        } else {
            throw new RuntimeException("Ошибка: нет эпика с таким id!");
        }
    }

    @Override
    public void updateStatusEpic(int id) {
        Epic epic = new Epic(id, epics.get(id).getName(),
                epics.get(id).getDescription());
        epic.setSubTasksId((ArrayList<Integer>) epics.get(id).getSubTasksID());
        int statusNew = 0;
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

    @Override
    public void removeTask(int id) {
        if (tasks.get(id) != null) {
            listOfTasksSortedByTime.remove(tasks.get(id));
            history.remove(id);
            tasks.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        if (subtasks.get(id) != null) {
            if (getSubTaskList(subtasks.get(id).getEpicID()) != null) {
                getSubTaskList(subtasks.get(id).getEpicID()).remove(Integer.valueOf(id));
                updateStatusEpic(subtasks.get(id).getEpicID());
                setStartTimeForEpic(subtasks.get(id).getEpicID());
                setEndTimeForEpic(subtasks.get(id).getEpicID());
                setDurationForEpic(subtasks.get(id).getEpicID());
                history.remove(id);
                subtasks.remove(id);
            }
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.get(id) != null) {
            listOfTasksSortedByTime.remove(epics.get(id));
            if (getSubTaskList(id) != null) {
                for (int subTaskID : getSubTaskList(id)) {
                    subtasks.remove(subTaskID);
                }
            }
            for (Integer subTaskId : epics.get(id).getSubTasksID()) {
                history.remove(subTaskId);
            }
            history.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void updateStatusSubTask(int id, Status subTaskStatus) {
        Subtask subtask = new Subtask(id, subtasks.get(id).getName(),
                subtasks.get(id).getDescription(),
                subTaskStatus,
                subtasks.get(id).getEpicID(),
                subtasks.get(id).getStartTime(),
                subtasks.get(id).getDuration());

        subtasks.put(subtask.getId(), subtask);
        updateStatusEpic(subtask.getEpicID());
    }

    public HistoryManager getHistory(){
        return history;
    }
    
    private void compareTasksByTimeAndAddToTreeSet(Task task) {
        listOfTasksSortedByTime.add(task);
        LocalDateTime prev = LocalDateTime.MIN;
        for (Task currentTask : listOfTasksSortedByTime) {
            if (currentTask.getStartTime() != null) {
                if (prev.isAfter(currentTask.getStartTime())) {
                    listOfTasksSortedByTime.remove(task);
                    throw new RuntimeException("Произошло пересечение задач по времени. Добавленная задача будет удалена.");
                }
                if (currentTask.getEndTime().isPresent()) {
                    prev = (currentTask.getEndTime()).get();
                }
            }
        }
    }

    private void setStartTimeForEpic(int epicId) {
        getEpicWithoutHistorySaving(epicId).getSubTasksID()
                .stream()
                .map(this::getSubtaskWithoutHistorySaving)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .ifPresent(getEpicWithoutHistorySaving(epicId)::setStartTime);
    }

    private void setEndTimeForEpic(int epicId) {
        getEpicWithoutHistorySaving(epicId).getSubTasksID()
                .stream()
                .map(this::getSubtaskWithoutHistorySaving)
                .map(x -> x.getEndTime().orElse(x.getStartTime()))
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(getEpicWithoutHistorySaving(epicId)::setEndTime);
    }

    private void setDurationForEpic(int epicId) {
        getEpicWithoutHistorySaving(epicId)
                .setDuration(getEpicWithoutHistorySaving(epicId).getSubTasksID()
                        .stream()
                        .map(this::getSubtaskWithoutHistorySaving)
                        .map(Task::getDuration)
                        .filter(Objects::nonNull)
                        .reduce(Duration.ZERO, Duration::plus));
    }

    private Subtask getSubtaskWithoutHistorySaving(int id) {
        return subtasks.get(id);
    }

    private Epic getEpicWithoutHistorySaving(int id) {
        return epics.get(id);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(listOfTasksSortedByTime);
    }
}

