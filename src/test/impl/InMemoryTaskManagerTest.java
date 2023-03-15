package test.impl;

import main.service.interfaces.TaskManager;

import static main.service.Managers.getDefaultInMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @Override
    TaskManager createTaskManager() {
        return getDefaultInMemoryTaskManager();
    }
}