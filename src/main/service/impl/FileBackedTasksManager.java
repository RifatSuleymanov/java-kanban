package main.service.impl;

import main.service.Managers;
import main.model.*;
import main.service.interfaces.HistoryManager;
import main.service.interfaces.TaskManager;
import main.service.exception.ManagerSaveException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private Path path;
    private URI uri;
    private static final String FIELD_SEPARATOR = ";";
    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 2;
    private static final int  DESCRIPTION_INDEX = 3;
    private static final int STATUS_INDEX = 4;
    private static final int EPIC_ID_INDEX = 5;
    private static final int START_TIME_ID_INDEX = 6;
    private static final int DURATION_ID_INDEX = 7;

    public FileBackedTasksManager(@NotNull File file) {
        this.path = file.toPath();
    }
    public FileBackedTasksManager(URI uri) {
        this.uri = uri;
    }

    public static void main(String[] args) throws Exception {
        FileBackedTasksManager manager1 = Managers.getDefaultSave();

        Task task1 = new Task("Съездить к родителям", "В выходные съездить в гости к родителям");
        manager1.addTask(task1);
        int task1ID = task1.getId();
        System.out.println("Таск №1 добавлен");

        Task task2 = new Task("Сделать потолки", "Установить натяжные потолки в квартире");
        manager1.addTask(task2);
        int task2ID = task2.getId();
        System.out.println("Таск №2 добавлен");
        System.out.println("***********************************************************************************");

        Epic epic1 = new Epic("Съездить за покупками", "Закупиться на месяц");
        manager1.addEpic(epic1);
        int epic1id = epic1.getId();
        System.out.println("Пустой эпик №1 добавлен");

        Epic epic2 = new Epic("ТО машины", "Отвезти машину в сервис для прохождении ТО");
        manager1.addEpic(epic2);
        int epicId2 = epic2.getId();
        System.out.println("Пустой эпик №2 добавлен");
        System.out.println("***********************************************************************************");

        SubTask subTask1 = new SubTask("Продуктовый магазин", "Молоко, сыр, хлеб, йогурты", epic1id);
        manager1.addSubTask(subTask1);
        int subTask1ID = subTask1.getId();
        System.out.println("Сабтаск №1 к эпику №1 добавлен");

        SubTask subTask2 = new SubTask("Хозтовары", "Порошок, мыло, гель для душа", epic1id);
        manager1.addSubTask(subTask2);
        int subTask2ID = subTask2.getId();
        System.out.println("Сабтаск №2 к эпику №1 добавлен");

        SubTask subTask3 = new SubTask("Купить корм для собаки", "Только хороший, а не Педигри", epic1id);
        manager1.addSubTask(subTask3);
        int subTask3ID = subTask3.getId();
        System.out.println("Сабтаск №2 к эпику №1 добавлен");
        System.out.println("***********************************************************************************");

        for (Integer key : manager1.getTasks().keySet()) {
            manager1.getTask(key).setTaskStatus(Status.DONE);
        }
        System.out.println("Установили статус DONE всем таскам.");

        for (Integer key : manager1.getEpic(epic1id).getSubTasksID()) {
            manager1.updateStatusSubTask(key, Status.DONE);
        }
        System.out.println("Установили статус DONE всем сабтаскам эпика про супермаркет.");
        System.out.println("Статус самого эпика тоже должен быть DONE.");
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем Таск №1");
        manager1.getTask(task1ID);
        System.out.println("Запрашиваем Таск №2");
        manager1.getTask(task2ID);
        System.out.println("Запрашиваем Эпик №2 ");
        manager1.getEpic(epicId2);
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем историю");
        System.out.println("Должно быть: Таск №1, Таск №2, Эпик №2");
        System.out.println(manager1.getHistory().getHistory());
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем Эпик №1");
        manager1.getEpic(epic1id);
        System.out.println("Запрашиваем Таск №2");
        manager1.getTask(task2ID);
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем историю");
        System.out.println("Должно быть: Таск №1, Эпик №2, Эпик №1, Таск №2");
        System.out.println(manager1.getHistory().getHistory());
        System.out.println("***********************************************************************************");

        System.out.println("Запрашиваем Сабтаск №3");
        manager1.getSubTask(subTask3ID);
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
        TaskManager manager2 = loadFromFile(new File("src/test/Test.csv"));
        System.out.println("Новый экземпляр менеджера создан");

        System.out.println("***********************************************************************************");

        System.out.println("Запросим его историю");
        System.out.println("Должно быть: Таск №1, Эпик №2, Эпик №1, Таск №2, Сабтаск №2, Сабтаск №1");
        System.out.println(manager2.getHistory().getHistory());
    }

    public void save() {
        try (Writer writer = new FileWriter(path.toString(), StandardCharsets.UTF_8)) {

            writer.write(String.join(FIELD_SEPARATOR, new String[]{
                    "id",
                    "TaskType",
                    "title",
                    "extraInfo",
                    "TaskStatus",
                    "epicID",
                    "startTime",
                    "duration",
                    "\n"}));

            for (Map.Entry<Integer, Task> entry : getTasks().entrySet()) {
                writer.append(getTaskString(entry.getValue()))
                        .write("\n");
            }
            for (Map.Entry<Integer, Epic> entry : getEpics().entrySet()) {
                writer.append(getTaskString(entry.getValue()))
                        .write("\n");
            }
            for (Map.Entry<Integer, SubTask> entry : getSubTasks().entrySet()) {
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
    public int addTask(Task task){
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubTask(SubTask subtask){
        super.addSubTask(subtask);
        save();
        return subtask.getId();
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
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public int updateTask(Task task) {
        super.updateTask(task);
        save();
        return task.getId();
    }

    @Override
    public int updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
        return subTask.getId();
    }
    @Override
    public void updateStatusEpic(int id) {
        super.updateStatusEpic(id);
        save();
    }

    @Override
    public void updateStatusSubTask(int id, Status subTaskStatus) {
        super.updateStatusSubTask(id, subTaskStatus);
        save();
    }

    public String getTaskString(@NotNull Task task) {
        if (task.getStartTime() == null) {
            return String.join(FIELD_SEPARATOR, new String[]{
                    Integer.toString(task.getId()),
                    task.getTaskType().name(),
                    task.getTitle(),
                    task.getExtraInfo(),
                    task.getTaskStatus().name(),
                    " ",
                    " ",
                    " "
            });
        } else {
            return String.join(FIELD_SEPARATOR, new String[]{
                    Integer.toString(task.getId()),
                    task.getTaskType().name(),
                    task.getTitle(),
                    task.getExtraInfo(),
                    task.getTaskStatus().name(),
                    " ",
                    task.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    String.valueOf(task.getDuration().toMinutes())
            });
        }
    }

    public String getTaskString(@NotNull Epic epic) {
        return String.join(FIELD_SEPARATOR, new String[]{
                Integer.toString(epic.getId()),
                epic.getTaskType().name(),
                epic.getTitle(),
                epic.getExtraInfo(),
                " ",
                " ",
                " ",
                " "
        });
    }

    public String getTaskString(@NotNull SubTask subTask) {
        if (subTask.getStartTime() == null) {
            return String.join(FIELD_SEPARATOR, new String[]{
                    Integer.toString(subTask.getId()),
                    subTask.getTaskType().name(),
                    subTask.getTitle(),
                    subTask.getExtraInfo(),
                    subTask.getTaskStatus().name(),
                    Integer.toString(subTask.getEpicID()),
                    " ",
                    " "
            });
        } else {
            return String.join(FIELD_SEPARATOR, new String[]{
                    Integer.toString(subTask.getId()),
                    subTask.getTaskType().name(),
                    subTask.getTitle(),
                    subTask.getExtraInfo(),
                    subTask.getTaskStatus().name(),
                    Integer.toString(subTask.getEpicID()),
                    subTask.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME),
                    String.valueOf(subTask.getDuration().toMinutes())
            });
        }
    }

    public Task getTaskFromString(String id, String title, String extraInfo,
                                  String status, String startTime, String duration) {
        if (startTime.equals(" ")) {
            return new Task(
                    Integer.parseInt(id),
                    title,
                    extraInfo,
                    Status.valueOf(status));
        } else {
            return new Task(
                    Integer.parseInt(id),
                    title,
                    extraInfo,
                    Status.valueOf(status),
                    LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME),
                    Duration.ofMinutes(Integer.parseInt(duration)));
        }
    }

    public Epic getEpicFromString(String id, String title, String extraInfo) {
        return new Epic(
                Integer.parseInt(id),
                title,
                extraInfo);
    }

    public SubTask getSubTaskFromString(String id, String title, String extraInfo,
                                        String status, String epicID, String startTime, String duration) {
        if (startTime.equals(" ")) {
            return new SubTask(
                    Integer.parseInt(id),
                    title,
                    extraInfo,
                    Status.valueOf(status),
                    Integer.parseInt(epicID));
        } else {
            return new SubTask(
                    Integer.parseInt(id),
                    title,
                    extraInfo,
                    Status.valueOf(status),
                    Integer.parseInt(epicID),
                    LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME),
                    Duration.ofMinutes(Integer.parseInt(duration)));
        }
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
        if (value != null && !value.isEmpty()) {
            List<Integer> historyList = Arrays.stream(value.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            return Optional.of(historyList);
        } else {
            return Optional.empty();
        }
    }

    public void addTaskByType(@NotNull String str) {
        String[] taskArray = str.split(FIELD_SEPARATOR);
        TaskType taskType = TaskType.valueOf(taskArray[1]);
        switch (taskType) {
            case TASK -> addTask(getTaskFromString(
                    taskArray[ID_INDEX],
                    taskArray[NAME_INDEX],
                    taskArray[DESCRIPTION_INDEX],
                    taskArray[STATUS_INDEX],
                    taskArray[START_TIME_ID_INDEX],
                    taskArray[DURATION_ID_INDEX]));
            case EPIC -> addEpic(getEpicFromString(
                    taskArray[ID_INDEX],
                    taskArray[NAME_INDEX],
                    taskArray[DESCRIPTION_INDEX]));
            case SUBTASK -> addSubTask(getSubTaskFromString(
                    taskArray[ID_INDEX],
                    taskArray[NAME_INDEX],
                    taskArray[DESCRIPTION_INDEX],
                    taskArray[STATUS_INDEX],
                    taskArray[EPIC_ID_INDEX],
                    taskArray[START_TIME_ID_INDEX],
                    taskArray[DURATION_ID_INDEX]));
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
                        Map<Integer, SubTask> subTaskMap = fileBackedTasksManager.getSubTasks();
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