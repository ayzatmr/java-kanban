import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import service.Managers;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("TASK 1", "DESCRIPTION 1");
        Task task2 = new Task("TASK 2", "DESCRIPTION 2");
        int firstTaskId = inMemoryTaskManager.addTask(task);
        int secondTaskId = inMemoryTaskManager.addTask(task2);

        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        Epic epic2 = new Epic("EPIC 2", "EPIC DESCRIPTION 2");
        int firstEpicId = inMemoryTaskManager.addEpic(epic1);
        int secondEpicId = inMemoryTaskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId());
        Subtask subtask3 = new Subtask("SUBTASK 3", "SUBTASK DESCRIPTION 3", epic1.getId());
        int firstSubtaskId = inMemoryTaskManager.addSubtask(subtask1);
        int secondSubtaskId = inMemoryTaskManager.addSubtask(subtask2);
        int thirdSubtaskId = inMemoryTaskManager.addSubtask(subtask3);

        System.out.println("\n" + "распечатать все задачи" + "\n");
        System.out.println(task);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);

        System.out.println("\n" + "проверка получения задач по идентификатору" + "\n");
        System.out.println(inMemoryTaskManager.getTaskById(firstTaskId));
        System.out.println(inMemoryTaskManager.getEpicById(firstEpicId));
        System.out.println(inMemoryTaskManager.getSubtaskById(firstSubtaskId));

        System.out.println("\n" + "распечатать просмотренные задачи" + "\n");
        List<Task> history = historyManager.getHistory();
        System.out.println(history);

        System.out.println("\n" + "проверка дубляжа просмотров");
        inMemoryTaskManager.getTaskById(firstTaskId);
        inMemoryTaskManager.getTaskById(secondTaskId);
        inMemoryTaskManager.getTaskById(firstTaskId);
        inMemoryTaskManager.getEpicById(firstEpicId);
        inMemoryTaskManager.getEpicById(firstEpicId);
        inMemoryTaskManager.getEpicById(secondEpicId);
        inMemoryTaskManager.getSubtaskById(secondSubtaskId);
        inMemoryTaskManager.getSubtaskById(thirdSubtaskId);
        inMemoryTaskManager.getSubtaskById(secondSubtaskId);

        System.out.println("\n" + "распечатать просмотренные задачи" + "\n");
        history = historyManager.getHistory();
        System.out.println(history);


        System.out.println("\n" + "удаление задачи по идентификатору" + "\n");
        inMemoryTaskManager.deleteEpicById(secondEpicId);
        inMemoryTaskManager.deleteSubtaskById(thirdSubtaskId);
        inMemoryTaskManager.deleteSubtaskById(secondSubtaskId);
        inMemoryTaskManager.deleteSubtaskById(firstSubtaskId);
        inMemoryTaskManager.deleteTaskById(secondTaskId);
        inMemoryTaskManager.deleteTaskById(secondTaskId); // проверка на npe

        System.out.println("\n" + "распечатать просмотренные задачи" + "\n");
        history = historyManager.getHistory();
        System.out.println(history);
    }
}
