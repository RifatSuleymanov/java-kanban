package service.impl;

import model.Task;
import service.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> requestHistory = new ArrayList<>();

    public static final int HISTORY_SIZE = 9;

    @Override
    public void add(Task task) {
        while (requestHistory.size() > HISTORY_SIZE){
            requestHistory.remove(0);
        }
        requestHistory.add(task);
    }

    @Override
    public List<Task> getRequestHistory() {
        return requestHistory;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InMemoryHistoryManager{");
        sb.append("requestHistory=").append(requestHistory);
        sb.append('}');
        return sb.toString();
    }
}
