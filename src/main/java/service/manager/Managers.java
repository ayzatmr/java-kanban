package service.manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public final class Managers {
    private Managers() {
    }

    public static TaskManager getDefault(String url) {
        return new HttpTaskManager(url);
    }

    public static TaskManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
