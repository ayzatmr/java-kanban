import enums.TaskStatus;
import enums.TaskType;
import models.Task;
import service.TaskService;

public class Main {
    public static void main(String[] args) {
        Task task = new Task(TaskType.TASK, "TASK 1", "DESCRIPTION 1", null);
        Task task2 = new Task(TaskType.TASK, "TASK 2", "DESCRIPTION 2", null);
        Task epic1 = new Task(TaskType.EPIC, "EPIC 1", "EPIC DESCRIPTION 1", null);
        Task epic2 = new Task(TaskType.EPIC, "EPIC 2", "EPIC DESCRIPTION 2", null);
        Task subtask1 = new Task(TaskType.SUBTASK, "SUBTASK 1", "SUBTASK DESCRIPTION 1", epic1.getId());
        Task subtask2 = new Task(TaskType.SUBTASK, "SUBTASK 2", "SUBTASK DESCRIPTION 2", epic1.getId());
        Task subtask3 = new Task(TaskType.SUBTASK, "SUBTASK 3", "SUBTASK DESCRIPTION 3", epic2.getId());

        TaskService taskService = new TaskService();
        taskService.addNewTask(task);
        taskService.addNewTask(task2);
        taskService.addNewTask(epic1);
        taskService.addNewTask(epic2);
        taskService.addNewTask(subtask1);
        taskService.addNewTask(subtask2);
        taskService.addNewTask(subtask3);

        System.out.println("распечатать все таски"+ "\n");
        System.out.println(task);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);


        System.out.println("проверка смены статусов"+ "\n");
        taskService.setTaskStatus(task, TaskStatus.IN_PROGRESS);
        taskService.setTaskStatus(task2, TaskStatus.DONE);
        taskService.setTaskStatus(epic1, TaskStatus.DONE);
        taskService.setTaskStatus(subtask1, TaskStatus.IN_PROGRESS);
        taskService.setTaskStatus(subtask3, TaskStatus.DONE);

        System.out.println(task);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);

        System.out.println("проверка получения задач по идентификатору" + "\n");
        System.out.println(taskService.getTaskById(task.getId()));
        System.out.println(taskService.getTaskById(subtask1.getId()));
        System.out.println(taskService.getTaskById(epic1.getId()));
        System.out.println(taskService.getEpicSubtasks(epic1.getId()));

        System.out.println("проверка получения задач по типу"+ "\n");
        System.out.println(taskService.getTasksByType(TaskType.EPIC));
        System.out.println(taskService.getTasksByType(TaskType.SUBTASK));

        System.out.println("удаление всех задач по типу"+ "\n");
        taskService.deleteAllTasksByType(TaskType.SUBTASK);
        System.out.println("Tasks= " + taskService.tasks);

        taskService.deleteAllTasksByType(TaskType.TASK);
        System.out.println("Tasks= " + taskService.tasks);

        System.out.println("удаление задачи по идентификатору"+ "\n");
        taskService.deleteTask(epic1);
        System.out.println("Tasks= " + taskService.tasks);
    }
}
