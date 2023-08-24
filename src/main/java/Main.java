import enums.TaskStatus;
import models.Epic;
import models.Subtask;
import models.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("TASK 1", "DESCRIPTION 1");
        Task task2 = new Task("TASK 2", "DESCRIPTION 2");
        Epic epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        Epic epic2 = new Epic("EPIC 2", "EPIC DESCRIPTION 2");

        TaskManager taskManager = new TaskManager();
        int firstTaskId = taskManager.addTask(task);
        int secondTaskId = taskManager.addTask(task2);
        int firstEpicId = taskManager.addEpic(epic1);
        int secondEpicId = taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        Subtask subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId());
        Subtask subtask3 = new Subtask("SUBTASK 3", "SUBTASK DESCRIPTION 3", epic2.getId());
        int firstSubtaskId = taskManager.addSubtask(subtask1);
        int secondSubtaskId = taskManager.addSubtask(subtask2);
        int thirdSubtaskId = taskManager.addSubtask(subtask3);

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
        taskManager.updateTask(newTask);

        Task newTask2 = new Task("TASK 2 updated", "DESCRIPTION 2 updated");
        newTask2.setTaskStatus(TaskStatus.DONE);
        newTask2.setId(secondTaskId);
        taskManager.updateTask(newTask2);

        Epic newEpic1 = new Epic("EPIC 1 updated", "EPIC DESCRIPTION 1 updated");
        newEpic1.setId(firstEpicId);
        taskManager.updateEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("SUBTASK 1 updated", "SUBTASK DESCRIPTION 1", epic1.getId());
        newSubtask1.setTaskStatus(TaskStatus.IN_PROGRESS);
        newSubtask1.setId(firstSubtaskId);
        taskManager.updateSubtask(newSubtask1);

        Subtask newSubtask3 = new Subtask("SUBTASK 3 updated", "SUBTASK DESCRIPTION 2", epic2.getId());
        newSubtask3.setTaskStatus(TaskStatus.DONE);
        newSubtask3.setId(thirdSubtaskId);
        taskManager.updateSubtask(newSubtask3);

        System.out.println("\n" + "Получить все задачи" + "\n");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n" + "проверка получения задач по идентификатору" + "\n");
        System.out.println(taskManager.getTaskById(firstTaskId));
        System.out.println(taskManager.getEpicById(firstEpicId));
        System.out.println(taskManager.getSubtaskById(firstSubtaskId));

        System.out.println("\n" + "удаление задачи по идентификатору" + "\n");
        taskManager.deleteEpicById(firstEpicId);
        taskManager.deleteSubtaskById(firstSubtaskId);
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n" + "удаление всех подзадач" + "\n");
        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
    }
}
