package service;

import interfaces.HistoryManager;
import models.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 9;

    @Override
    public void add(Task task) {
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
