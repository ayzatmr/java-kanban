package service;

import interfaces.HistoryManager;
import models.Task;
import utils.CustomLinkedList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static final CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        history.linkLast(task.getId(), task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history.getTasks());
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.get(id));
    }
}
