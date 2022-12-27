package service.impl;

import service.HistoryManager;
import service.TaskManager;

public abstract class Managers implements TaskManager {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
