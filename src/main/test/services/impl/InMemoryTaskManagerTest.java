package main.test.services.impl;

import main.service.TaskManager;

import static main.service.impl.Managers.getDefault;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @Override
    TaskManager createTaskManager() {
        return getDefault();
    }
}