package test.services.impl;

import main.service.TaskManager;

import static main.service.impl.Managers.getDefault;
import static main.service.impl.Managers.getDefaultSave;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @Override
    TaskManager createTaskManager() {
        return getDefault();
    }
}