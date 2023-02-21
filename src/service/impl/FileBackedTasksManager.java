package service.impl;

import model.*;

import org.jetbrains.annotations.NotNull;
import service.HistoryManager;
import service.TaskManager;
import service.exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;  // Путь для сохранения файла
    private final String divide = ";";   // Разделитель

    public FileBackedTasksManager(@NotNull File file) {
        this.path = file.toPath();
    }

    public static void main(String[] args) throws Exception {
        FileBackedTasksManager manager1 = Managers.getDefaultSave();

        Task task1 = new Task("Съездить к родителям", "В выходные съездить в гости к родителям");
        manager1.addTaskWithID(task1);
        int task1ID = task1.getId();
        System.out.println("Таск №1 добавлен");

        Task task2 = new Task("Сделать потолки", "Установить натяжные потолки в квартире");
        manager1.addTaskWithID(task2);
        int task2ID = task2.getId();
        System.out.println("Таск №2 добавлен");
        System.out.println("-----------------------------------------------------------------------------------------");

        Epic epic1 = new Epic("Съездить за покупками", "Закупиться на месяц");
        manager1.addEpicWithID(epic1);
        int epicId1 = epic1.getId();
        System.out.println("Пустой эпик №1 добавлен");

        Epic epic2 = new Epic("ТО машины", "Отвезти машину в сервис для прохождении ТО");
        manager1.addEpicWithID(epic2);
        int epicId2 = epic2.getId();
        System.out.println("Пустой эпик №2 добавлен");
        System.out.println("-----------------------------------------------------------------------------------------");

        Subtask subTask1 = new Subtask("Продуктовый магазин", "Молоко, сыр, хлеб, йогурты", epicId1);
        manager1.addSubTaskWithID(subTask1);
        int subTask1ID = subTask1.getId();
        System.out.println("Сабтаск №1 к эпику №1 добавлен");

        Subtask subTask2 = new Subtask("Хозтовары", "Порошок, мыло, гель для душа", epicId1);
        manager1.addSubTaskWithID(subTask2);
        int subTask2ID = subTask2.getId();
        System.out.println("Сабтаск №2 к эпику №1 добавлен");

        for (Integer key : manager1.getTasks().keySet()) {
            manager1.getTask(key).setTaskStatus(Status.DONE);
        }
        System.out.println("Установили статус DONE всем таскам.");

        for (Integer key : manager1.getEpic(epicId1).getSubTasksID()) {
            manager1.updateStatusSubTask(key, Status.DONE);
        }
        System.out.println("Установили статус DONE всем сабтаскам эпика про супермаркет.");
        System.out.println("Статус самого эпика тоже должен быть DONE.");
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем Таск №1");
        manager1.getTask(task1ID);
        System.out.println("Запрашиваем Таск №2");
        manager1.getTask(task2ID);
        System.out.println("Запрашиваем Эпик №2 (ПУСТОЙ)");
        manager1.getEpic(epicId2);
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем историю");
        System.out.println("Должно быть: Таск №1, Таск №2, Эпик №2");
        System.out.println(manager1.getHistory().getHistory());
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем Эпик №1");
        manager1.getEpic(epicId1);
        System.out.println("Запрашиваем Таск №2");
        manager1.getTask(task2ID);
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем историю");
        System.out.println("Должно быть: Таск №1, Эпик №2, Эпик №1, Таск №2");
        System.out.println(manager1.getHistory().getHistory());
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем Сабтаск №2");
        manager1.getSubTask(subTask2ID);
        System.out.println("Запрашиваем Сабтаск №1");
        manager1.getSubTask(subTask1ID);
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем историю");
        System.out.println("Должно быть: Таск №1, Эпик №2, Эпик №1, Таск №2 Сабтаск №2, Сабтаск №1");
        System.out.println(manager1.getHistory().getHistory());
        System.out.println("***********************************************************************************");
        System.out.println("Создаем новый экземпляр менеджера из файла");
        TaskManager manager2 = loadFromFile(new File("file.file.csv"));
        System.out.println("Новый экземпляр менеджера создан");

        System.out.println("***********************************************************************************");

        System.out.println("Запросим его историю");
        System.out.println("Должно быть: Таск №1, Эпик №2, Эпик №1, Таск №2, Сабтаск №2, Сабтаск №1");
        System.out.println(manager2.getHistory().getHistory());
    }

    public void save() {
        try (Writer writer = new FileWriter(path.toString(), StandardCharsets.UTF_8)) {

            writer.write(String.join(divide, new String[]{
                    "id",
                    "TaskType",
                    "title",
                    "extraInfo",
                    "TaskStatus",
                    "epicID",
                    "\n"}));

            for (Map.Entry<Integer, Task> entry : getTasks().entrySet()) {
                writer.append(getTaskString(entry.getValue()))
                        .write("\n");
            }
            for (Map.Entry<Integer, Epic> entry : getEpics().entrySet()) {
                writer.append(getTaskString(entry.getValue()))
                        .write("\n");
            }
            for (Map.Entry<Integer, Subtask> entry : getSubTasks().entrySet()) {
                writer.append(getTaskString(entry.getValue()))
                        .write("\n");
            }
            writer.append("\n").
                    write(historyToString(getHistory()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public void createTask(Task task) {
        getTasks().put(task.getId(), task);
        save();
    }

    public void addTaskWithID(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        getEpics().put(epic.getId(), epic);
        save();
    }

    public void addEpicWithID(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtasks(@NotNull Subtask subTask) {
        if (!getEpics().containsKey(subTask.getEpicID())) {
            throw new RuntimeException("Ошибка: эпик отсутствует!");
        }
        super.getSubTasks().put(subTask.getId(), subTask);
        getSubTaskList(subTask.getEpicID()).add(subTask.getId());
        updateStatusEpic(subTask.getEpicID());
        save();
    }

    public void addSubTaskWithID(Subtask subTask) {
        super.createSubtasks(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubTask(int id) {
        Subtask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void updateStatusEpic(int id) {
        super.updateStatusEpic(id);
        save();
    }

    @Override
    public void updateStatusSubTask(int id, Status subTaskStatus) throws Exception {
        super.updateStatusSubTask(id, subTaskStatus);
        save();
    }

    public String getTaskString(@NotNull Task task) {
        return String.join(divide, new String[]{
                Integer.toString(task.getId()),
                task.getTaskType().name(),
                task.getTitle(),
                task.getExtraInfo(),
                task.getTaskStatus().name(),
                " "
        });
    }

    public String getTaskString(@NotNull Epic epic) {
        return String.join(divide, new String[]{
                Integer.toString(epic.getId()),
                epic.getTaskType().name(),
                epic.getTitle(),
                epic.getExtraInfo(),
                " ",
                " "
        });
    }

    public String getTaskString(@NotNull Subtask subTask) {
        return String.join(divide, new String[]{
                Integer.toString(subTask.getId()),
                subTask.getTaskType().name(),
                subTask.getTitle(),
                subTask.getExtraInfo(),
                subTask.getTaskStatus().name(),
                Integer.toString(subTask.getEpicID())
        });
    }

    public Task getTaskFromString(String id, String name, String description, String status) {
        return new Task(
                Integer.parseInt(id),
                name,
                description,
                Status.valueOf(status));
    }

    public Epic getEpicFromString(String id, String name, String description) {
        return new Epic(
                Integer.parseInt(id),
                name,
                description);
    }

    public Subtask getSubtaskFromString(String id, String name, String description, String status, String epicId) {
        return new Subtask(
                Integer.parseInt(id),
                name,
                description,
                Status.valueOf(status),
                Integer.parseInt(epicId));
    }

    public String historyToString(@NotNull HistoryManager manager) {
        if (manager.getHistory().size() != 0) {
            List<Task> tasks = manager.getHistory();
            StringBuilder stringBuilder = new StringBuilder(Integer.toString(tasks.get(0).getId()));
            for (int i = 1; i < tasks.size(); i++) {
                stringBuilder.append(",").append(tasks.get(i).getId());
            }
            return stringBuilder.toString();
        } else {
            return "\n";
        }

    }

    public static Optional<List<Integer>> historyListFromString(String value) {
        if (value != null) {
            List<Integer> historyList = Arrays.stream(value.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return Optional.of(historyList);
        } else {
            return Optional.empty();
        }
    }

    public void addTaskByType(@NotNull String str) {
        String[] taskArray = str.split(divide);
        TaskType taskType = TaskType.valueOf(taskArray[1]);
        switch (taskType) {
            case TASK -> createTask(getTaskFromString(
                    taskArray[0],
                    taskArray[2],
                    taskArray[3],
                    taskArray[4]));
            case EPIC -> createEpic(getEpicFromString(
                    taskArray[0],
                    taskArray[2],
                    taskArray[3]));
            case SUBTASK -> createSubtasks(getSubtaskFromString(
                    taskArray[0],
                    taskArray[2],
                    taskArray[3],
                    taskArray[4],
                    taskArray[5]));
        }
    }

    public static @NotNull FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        if (Files.exists(file.toPath())) {
            try (Reader fileReader = new FileReader(file.toPath().toString(), StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                bufferedReader.readLine();
                while (bufferedReader.ready()) {
                    String str = bufferedReader.readLine();
                    if (str != null && !str.isEmpty()) {
                        fileBackedTasksManager.addTaskByType(str);
                    } else if (str != null && str.isEmpty()) {
                        String historyLine = bufferedReader.readLine();
                        List<Integer> historyList = historyListFromString(historyLine).orElse(Collections.emptyList());
                        Map<Integer, Task> taskMap = fileBackedTasksManager.getTasks();
                        Map<Integer, Epic> epicMap = fileBackedTasksManager.getEpics();
                        Map<Integer, Subtask> subTaskMap = fileBackedTasksManager.getSubTasks();
                        for (Integer id : historyList) {
                            if (taskMap.containsKey(id)) {
                                fileBackedTasksManager.getTask(id);
                            } else if (epicMap.containsKey(id)) {
                                fileBackedTasksManager.getEpic(id);
                            } else if (subTaskMap.containsKey(id)) {
                                fileBackedTasksManager.getSubTask(id);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileBackedTasksManager;
    }
}
