package service.impl;

import service.HistoryManager;
import service.TaskManager;

import java.io.File;

public abstract class Managers implements TaskManager {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultSave() {
        return new FileBackedTasksManager(new File("src/file.file.csv"));
    }
}
