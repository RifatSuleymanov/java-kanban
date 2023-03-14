package main.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.model.DurationAdapter;
import main.model.LocalDateTimeAdapter;
import main.service.impl.FileBackedTasksManager;
import main.service.impl.HttpTaskManager;
import main.service.impl.InMemoryHistoryManager;
import main.service.impl.InMemoryTaskManager;
import main.service.interfaces.HistoryManager;
import main.service.interfaces.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Managers implements TaskManager {

    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager(URI.create("http://localhost:8078/"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultSave() {
        return new FileBackedTasksManager(new File("src/main/service/impl/Test.csv"));
    }
    public static TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting();
        return gsonBuilder.create();
    }
}