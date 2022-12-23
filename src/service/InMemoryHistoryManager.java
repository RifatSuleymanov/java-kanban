package service;

import model.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> requestHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        while (requestHistory.size() > 9){
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
