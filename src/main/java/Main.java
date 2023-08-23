import enums.TaskStatus;
import enums.TaskType;
import models.Epic;
import models.Subtask;
import models.Task;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        Task task = new Task("TASK 1", "DESCRIPTION 1");
        Task task2 = new Task("TASK 2", "DESCRIPTION 2");
        Task epic1 = new Epic("EPIC 1", "EPIC DESCRIPTION 1");
        Task epic2 = new Epic("EPIC 2", "EPIC DESCRIPTION 2");

        TaskManager taskManager = new TaskManager();
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addTask(epic1);
        taskManager.addTask(epic2);

        Task subtask1 = new Subtask("SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        Task subtask2 = new Subtask("SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId());
        Task subtask3 = new Subtask("SUBTASK 3", "SUBTASK DESCRIPTION 3", epic2.getId());
        taskManager.addTask(subtask1);
        taskManager.addTask(subtask2);
        taskManager.addTask(subtask3);

        System.out.println("\n" + "распечатать все задачи" + "\n");
        System.out.println(task);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);


        System.out.println("\n" + "проверка смены статусов" + "\n");
        taskManager.setTaskStatus(task, TaskStatus.IN_PROGRESS);
        taskManager.setTaskStatus(task2, TaskStatus.DONE);
        taskManager.setTaskStatus(epic1, TaskStatus.DONE);
        taskManager.setTaskStatus(subtask1, TaskStatus.IN_PROGRESS);
        taskManager.setTaskStatus(subtask3, TaskStatus.DONE);

        System.out.println(task);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);

        System.out.println("\n" + "Получить все задачи" + "\n");
        System.out.println(taskManager.getAllTasks(TaskType.EPIC));

        System.out.println("\n" + "проверка получения задач по идентификатору" + "\n");
        System.out.println(taskManager.getTaskById(task.getId(), TaskType.TASK));
        System.out.println(taskManager.getTaskById(subtask1.getId(), TaskType.SUBTASK));
        System.out.println(taskManager.getTaskById(epic1.getId(), TaskType.EPIC));
        System.out.println(taskManager.getEpicSubtasks(epic1.getId()));

        System.out.println("\n" + "удаление задачи по идентификатору" + "\n");
        taskManager.deleteTaskById(epic1.getId(), TaskType.EPIC);
        taskManager.deleteAllTasksByType(TaskType.TASK);
        System.out.println("Tasks= " + taskManager.tasks);
        System.out.println("Tasks= " + taskManager.subtasks);
        System.out.println("Tasks= " + taskManager.epics);
    }
}
