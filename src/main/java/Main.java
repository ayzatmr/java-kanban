import enums.TaskStatus;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import service.Managers;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("TASK 1", "DESCRIPTION 1");
        Task task2 = new Task("TASK 2", "DESCRIPTION 2");
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        Epic epic2 = new Epic("EPIC 2", "EPIC DESCRIPTION 2");


        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        int firstTaskId = inMemoryTaskManager.addTask(task);
        int secondTaskId = inMemoryTaskManager.addTask(task2);
        int firstEpicId = inMemoryTaskManager.addEpic(epic1);
        int secondEpicId = inMemoryTaskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId());
        Subtask subtask3 = new Subtask("SUBTASK 3", "SUBTASK DESCRIPTION 3", epic2.getId());
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

        System.out.println("\n" + "проверка смены статусов" + "\n");

        Task newTask = new Task("TASK 1 updated", "DESCRIPTION 1 updated");
        newTask.setTaskStatus(TaskStatus.IN_PROGRESS);
        newTask.setId(firstTaskId);
        inMemoryTaskManager.updateTask(newTask);

        Task newTask2 = new Task("TASK 2 updated", "DESCRIPTION 2 updated");
        newTask2.setTaskStatus(TaskStatus.DONE);
        newTask2.setId(secondTaskId);
        inMemoryTaskManager.updateTask(newTask2);

        Epic newEpic1 = new Epic("EPIC 1 updated", "EPIC DESCRIPTION 1 updated");
        newEpic1.setId(firstEpicId);
        inMemoryTaskManager.updateEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("SUBTASK 1 updated", "SUBTASK DESCRIPTION 1", epic1.getId());
        newSubtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        newSubtask1.setId(firstSubtaskId);
        inMemoryTaskManager.updateSubtask(newSubtask1);

        Subtask newSubtask3 = new Subtask("SUBTASK 3 updated", "SUBTASK DESCRIPTION 2", epic2.getId());
        newSubtask3.setTaskStatus(TaskStatus.DONE);
        newSubtask3.setId(thirdSubtaskId);
        inMemoryTaskManager.updateSubtask(newSubtask3);

        System.out.println("\n" + "Получить все задачи" + "\n");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        System.out.println(inMemoryTaskManager.getAllEpics());

        System.out.println("\n" + "проверка получения задач по идентификатору" + "\n");
        System.out.println(inMemoryTaskManager.getTaskById(firstTaskId));
        System.out.println(inMemoryTaskManager.getEpicById(firstEpicId));
        System.out.println(inMemoryTaskManager.getSubtaskById(firstSubtaskId));

        System.out.println("\n" + "распечатать просмотренные задачи" + "\n");
        List<Task> history = historyManager.getHistory();
        System.out.println(history);

        inMemoryTaskManager.getTaskById(firstTaskId);
        inMemoryTaskManager.getTaskById(secondTaskId);
        inMemoryTaskManager.getTaskById(firstTaskId);
        inMemoryTaskManager.getTaskById(firstTaskId);
        inMemoryTaskManager.getTaskById(firstTaskId);
        inMemoryTaskManager.getTaskById(secondTaskId);
        inMemoryTaskManager.getTaskById(secondTaskId);
        inMemoryTaskManager.getTaskById(secondTaskId);
        inMemoryTaskManager.getTaskById(secondTaskId);

        System.out.println("\n" + "проверить удаление задач из просмотренных" + "\n");
        System.out.println(history);

        System.out.println("\n" + "удаление задачи по идентификатору" + "\n");
        inMemoryTaskManager.deleteEpicById(firstEpicId);
        inMemoryTaskManager.deleteSubtaskById(firstSubtaskId);
        System.out.println(inMemoryTaskManager.getAllEpics());

        System.out.println("\n" + "удаление всех подзадач" + "\n");
        inMemoryTaskManager.deleteAllSubtasks();
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllTasks());

    }
}
