package test.services.impl;

import main.model.Subtask;
import main.service.impl.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


import static main.service.impl.Managers.getDefaultSave;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{

    @Override
    FileBackedTasksManager createTaskManager() {
        return getDefaultSave();
    }

    @AfterEach
    public void deleteCSV() {
        File tmp = new File("src/main/service/impl/Test.csv");
        try (RandomAccessFile raf = new RandomAccessFile(tmp, "rw")) {
            if (tmp.exists()) {
                raf.setLength(0);
            }
        } catch (IOException exception) {
            System.out.println("Ошибка при очистке файла между тестами.");
        }
    }

    @Test
    void shouldSaveAndRestoreManagerWithEmptyHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Title for SubTask-1", "Description for SubTask-1",
                epic1id, dateTime3, duration10);
        Subtask subTask2 = new Subtask("Title for SubTask-2", "Description for SubTask-2",
                epic1id, dateTime4, duration20);
        taskManager.createSubtasks(subTask1);
        taskManager.createSubtasks(subTask2);

        FileBackedTasksManager restoredManager = FileBackedTasksManager
                .loadFromFile(new File("src/main/service/impl/Test.csv"));

        assertEquals(2, restoredManager.getTasks().size(), "Число тасков отличается от исходного.");
        assertEquals(1, restoredManager.getEpics().size(), "Число эпиков отличается от исходного.");
        assertEquals(2, restoredManager.getSubTasks().size(), "Число сабТасков отличается от исходного.");
    }

    @Test
    void shouldReturnEmptyHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Title for SubTask-1", "Description for SubTask-1",
                epic1id, dateTime3, duration10);
        Subtask subTask2 = new Subtask("Title for SubTask-2", "Description for SubTask-2",
                epic1id, dateTime4, duration20);
        taskManager.createSubtasks(subTask1);
        taskManager.createSubtasks(subTask2);

        FileBackedTasksManager restoredManager = FileBackedTasksManager
                .loadFromFile(new File("src/main/service/impl/Test.csv"));

        assertEquals(0, taskManager.getHistory().getHistory().size(),
                "Размер списка истории у исходного менеджера отличается от нуля.");
        assertEquals(0, restoredManager.getHistory().getHistory().size(),
                "Размер списка истории у восстановленного менеджера отличается от нуля.");
    }

    @Test
    void shouldSaveAndRestoreManagerWithExistingHistory() {
        int task1id = taskManager.createTask(task1);
        int task2id = taskManager.createTask(task2);
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Title for SubTask-1", "Description for SubTask-1",
                epic1id, dateTime3, duration10);
        Subtask subTask2 = new Subtask("Title for SubTask-2", "Description for SubTask-2",
                epic1id, dateTime4, duration20);
        final int subTask1id = taskManager.createSubtasks(subTask1);
        final int subTask2id = taskManager.createSubtasks(subTask2);
        taskManager.getSubTask(subTask2id);
        taskManager.getSubTask(subTask1id);
        taskManager.getEpic(epic1id);
        taskManager.getTask(task2id);
        taskManager.getTask(task1id);
        FileBackedTasksManager restoredManager = FileBackedTasksManager
                .loadFromFile(new File("src/main/service/impl/Test.csv"));


        assertEquals(5, restoredManager.getHistory().getHistory().size(),
                "Размер списка истории у восстановленного менеджера отличается от ожидаемого.");
        assertEquals(subTask2id, restoredManager.getHistory().getHistory().get(0).getId(),
                "Ошибка сохранения истории: нарушен порядок задач.");
        assertEquals(task1id, restoredManager.getHistory().getHistory()
                        .get(restoredManager.getHistory().getHistory().size() - 1).getId(),
                "Ошибка сохранения истории: нарушен порядок задач.");
    }
}
