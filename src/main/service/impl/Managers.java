package main.service.impl;

import main.service.HistoryManager;
import main.service.TaskManager;

import java.io.File;

public abstract class Managers implements TaskManager {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultSave() {
        return new FileBackedTasksManager(new File("src/main/service/impl/Test.csv"));
    }
}
