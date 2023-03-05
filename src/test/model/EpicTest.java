package test.model;

import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.service.TaskManager;
import main.service.impl.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    private final File file = new File("src/test/exampleTest.csv");
    private TaskManager taskManager;

    @BeforeEach
    public void createNewFileBackedTasksManager() {
        taskManager = Managers.getDefaultSave();
    }

    @AfterEach
    public void deleteFile() {
        file.delete();
    }

    @Test
    public void statusShouldBeNewWhenEpicHaveNoSubtasks() {
        Epic epic1 = new Epic("Заголовок эпика", "Текст описания эпика");
        int epic1id = taskManager.createEpic(epic1);

        Status epicsStatus = taskManager.getEpic(epic1id).getStatus();

        assertEquals(epicsStatus, Status.NEW, "Статус эпика не соответствует ожидаемому.");
    }

    @Test
    public void statusShouldBeNewWhenAllSubtasksHaveStatusNew() {
        Epic epic1 = new Epic("Заголовок эпика", "Текст описания эпика");
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Заг сабтаска-1", "Текст сабтаска-1", epic1id);
        Subtask subTask2 = new Subtask("Заг сабтаска-2", "Текст сабтаска-2", epic1id);
        taskManager.createSubtasks(subTask1);
        taskManager.createSubtasks(subTask2);

        Status epicsStatus = taskManager.getEpic(epic1id).getStatus();

        assertEquals(epicsStatus, Status.NEW, "Статус эпика не соответствует ожидаемому.");
    }

    @Test
    public void statusShouldBeDoneWhenAllSubtasksHaveStatusDone() {
        Epic epic1 = new Epic("Заголовок эпика", "Текст описания эпика");
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Заг сабтаска-1", "Текст сабтаска-1", epic1id);
        Subtask subTask2 = new Subtask("Заг сабтаска-2", "Текст сабтаска-2", epic1id);
        taskManager.createSubtasks(subTask1);
        taskManager.createSubtasks(subTask2);
        taskManager.getEpic(epic1id).getSubTasksID()
                .forEach(key -> taskManager.updateStatusSubTask(key, Status.DONE));

        Status epicStatus = taskManager.getEpic(epic1id).getStatus();

        assertEquals(epicStatus, Status.DONE, "Статус эпика не соответствует ожидаемому.");
    }

    @Test
    public void statusShouldBeInProgressWhenSubtasksHaveStatusNewAndDone() {
        Epic epic1 = new Epic("Заголовок эпика", "Текст описания эпика");
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Заг сабтаска-1", "Текст сабтаска-1", epic1id);
        Subtask subTask2 = new Subtask("Заг сабтаска-2", "Текст сабтаска-2", epic1id);
        int subTask1id = taskManager.createSubtasks(subTask1);
        int subTask2id = taskManager.createSubtasks(subTask2);
        taskManager.updateStatusSubTask(subTask2id, Status.DONE);

        Status epicStatus = taskManager.getEpic(epic1id).getStatus();
        Status subTask1Status = taskManager.getSubTask(subTask1id).getStatus();
        Status subTask2Status = taskManager.getSubTask(subTask2id).getStatus();

        assertEquals(subTask1Status, Status.NEW);
        assertEquals(subTask2Status, Status.DONE);
        assertEquals(epicStatus, Status.DONE, "Статус эпика не соответствует ожидаемому.");
    }

    @Test
    public void statusShouldBeInProgressWhenAllSubtasksHaveStatusInProgress() {
        Epic epic1 = new Epic("Заголовок эпика", "Текст описания эпика");
        int epic1id = taskManager.createEpic(epic1);
        Subtask subTask1 = new Subtask("Заг сабтаска-1", "Текст сабтаска-1", epic1id);
        Subtask subTask2 = new Subtask("Заг сабтаска-2", "Текст сабтаска-2", epic1id);
        taskManager.createSubtasks(subTask1);
        taskManager.createSubtasks(subTask2);
        taskManager.getEpic(epic1id).getSubTasksID()
                .forEach(key -> taskManager.updateStatusSubTask(key, Status.IN_PROGRESS));

        Status epicStatus = taskManager.getEpic(epic1.getId()).getStatus();

        assertEquals(epicStatus, Status.IN_PROGRESS, "Статус эпика не соответствует ожидаемому.");
    }

}