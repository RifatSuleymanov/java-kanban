package main.service.impl;

import main.service.Managers;
import main.model.Epic;
import main.model.Status;
import main.model.SubTask;
import main.model.Task;
import main.service.interfaces.HistoryManager;
import main.service.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
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
    public int addTask(Task task) {
        if (task.getId() == -1) {
            task.setId(counter++);
        }
        tasks.put(task.getId(), task);
        try {
            compareTasksByTimeAndAddToTreeSet(task);
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic.getId() == -1) {
            epic.setId(counter++);
        }
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicID())) {
            throw new RuntimeException("Ошибка: родительский эпик отсутствует!");
        }
        if (subTask.getId() == -1) {
            subTask.setId(counter++);
        }
        subtasks.put(subTask.getId(), subTask);
        if (!getSubTaskList(subTask.getEpicID()).contains(subTask.getId())) {
            getSubTaskList(subTask.getEpicID()).add(subTask.getId());
        }
        updateStatusEpic(subTask.getEpicID());
        setStartTimeForEpic(subTask.getEpicID());
        setEndTimeForEpic(subTask.getEpicID());
        setDurationForEpic(subTask.getEpicID());

        try {
            compareTasksByTimeAndAddToTreeSet(subTask);
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
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
    public SubTask getSubTask(int id) {
        if (subtasks.get(id) != null) {
            history.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new RuntimeException("Ошибка: нет сабтаска с таким id!");
        }
    }

    @Override
    public HashMap<Integer, Task> getTasks() {                  // Получение хешмапы всех тасков
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {                  // Получение хешмапы всех эпиков
        return epics;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {            // Получение хешмапы всех сабтасков
        return subtasks;
    }

    @Override
    public int updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            listOfTasksSortedByTime.remove(task);
            tasks.put(task.getId(), task);
            try {
                compareTasksByTimeAndAddToTreeSet(task);
            } catch (RuntimeException exception) {
                System.out.println(exception.getMessage());
            }
        }
        return task.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            listOfTasksSortedByTime.remove(subTask);
            if (!epics.containsKey(subTask.getEpicID())) {
                throw new RuntimeException("Ошибка: родительский эпик отсутствует!");
            }
            subtasks.put(subTask.getId(), subTask);
            updateStatusEpic(subTask.getEpicID());
            setStartTimeForEpic(subTask.getEpicID());
            setEndTimeForEpic(subTask.getEpicID());
            setDurationForEpic(subTask.getEpicID());

            try {
                compareTasksByTimeAndAddToTreeSet(subTask);
            } catch (RuntimeException exception) {
                System.out.println(exception.getMessage());
            }
        }
        return subTask.getId();
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
        for (SubTask subTask : subtasks.values()) {
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

        for (SubTask subTask : subtasks.values()) {
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
    public ArrayList<Integer> getSubTaskList(int epicID) {
        if (epics.get(epicID) != null) {
            return epics.get(epicID).getSubTasksID();
        } else {
            throw new RuntimeException("Ошибка: нет эпика с таким id!");
        }
    }

    @Override
    public void updateStatusEpic(int id) { // Обновить статус эпика
        Epic epic = new Epic(id, epics.get(id).getTitle(),
                epics.get(id).getExtraInfo());
        epic.setSubTasksId(epics.get(id).getSubTasksID());

        int statusNew = 0;
        int statusInProgress = 0;
        int statusDone = 0;
        if (epic.getSubTasksID() != null) {
            for (int i = 0; i < epic.getSubTasksID().size(); i++) {
                if (subtasks.get(epic.getSubTasksID().get(i)) != null) {
                    if (subtasks.get(epic.getSubTasksID().get(i)).getTaskStatus() == Status.NEW) {
                        statusNew++;
                    } else if (subtasks.get(epic.getSubTasksID().get(i)).getTaskStatus() == Status.IN_PROGRESS) {
                        statusInProgress++;
                    } else {
                        statusDone++;
                    }
                }
            }

            if (statusInProgress == 0 && statusDone == 0) {
                epic.setTaskStatus(Status.NEW);
            } else if (statusDone > 0 && (statusNew < 1 && statusInProgress < 1)) {
                epic.setTaskStatus(Status.DONE);
            } else {
                epic.setTaskStatus(Status.IN_PROGRESS);
            }
        } else {
            epic.setTaskStatus(Status.NEW);
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
            listOfTasksSortedByTime.remove(subtasks.get(id));
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
        SubTask subTask = new SubTask(id, subtasks.get(id).getTitle(),
                subtasks.get(id).getExtraInfo(),
                subTaskStatus,
                subtasks.get(id).getEpicID(),
                subtasks.get(id).getStartTime(),
                subtasks.get(id).getDuration());

        subtasks.put(subTask.getId(), subTask);
        updateStatusEpic(subTask.getEpicID());
    }

    @Override
    public HistoryManager getHistory() {
        return history;
    }

    void compareTasksByTimeAndAddToTreeSet(Task task) {
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
                .map(SubTask::getStartTime)
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

    private SubTask getSubtaskWithoutHistorySaving(int id) {
        return subtasks.get(id);
    }

    private Epic getEpicWithoutHistorySaving(int id) {
        return epics.get(id);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(listOfTasksSortedByTime);
    }
}