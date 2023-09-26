package service;

import interfaces.HistoryManager;
import models.Task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static final HashMap<Integer, Task> history = new LinkedHashMap<>();

    @Override
    public void add(Task task) {
        if (history.containsValue(task)) {
            this.remove(task.getId());
        }
        history.put(task.getId(), task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history.values());
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}
