import service.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) throws Exception {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Съездить к родителям", "В выходные съездить в гости к родителям");
        taskManager.createTask(task1);
        int task1ID = task1.getId();

        Task task2 = new Task("Сделать потолки", "Установить натяжные потолки в квартире");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Съездить за покупками", "Закупиться на месяц");
        taskManager.createEpic(epic1);
        int epicId1 = epic1.getId();

        Epic epic2 = new Epic("ТО машины", "Отвезти машину в сервис для прохождении ТО");
        taskManager.createEpic(epic2);
        int epicId2 = epic2.getId();

        Subtask subtask1 = new Subtask("Продуктовый магазин", "Молоко, сыр, хлеб, йогурты", epicId1);
        taskManager.createSubtasks(subtask1);
        int subTaskId1 = subtask1.getId();

        Subtask subtask2 = new Subtask("Хозтовары", "Порошок, мыло, гель для душа", epicId1);
        taskManager.createSubtasks(subtask2);
        int subTaskId2 = subtask2.getId();

        Subtask subtask3 = new Subtask("Замена масло", "Было налито масло Лукойл", epicId2);
        taskManager.createSubtasks(subtask3);
        int subTaskId3 = subtask3.getId();

        Subtask subtask4 = new Subtask("Замена фильтра", "Заменили масляной фильтр", epicId2);
        taskManager.createSubtasks(subtask4);
        int subTaskId4 = subtask4.getId();

        System.out.println("Список дел:");
        for (Integer key : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список задач:");
        for (Integer key : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : taskManager.getSubTasks().keySet()) {
            System.out.println(taskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Получение одного дела по ID:");
        System.out.println(taskManager.getTask(task1ID));
        System.out.println("***********************************************************************************");

        System.out.println("Получение одной задачи по ID:");
        System.out.println(taskManager.getEpic(epicId1));
        System.out.println("***********************************************************************************");

        System.out.println("Получение одной подзадачи по ID:");
        System.out.println(taskManager.getSubTask(subTaskId1));
        System.out.println("***********************************************************************************");

        System.out.println("Поставили статус DONE всем делам.");
        for (Integer key : taskManager.getTasks().keySet()) {
            taskManager.getTask(key).setTaskStatus(Status.DONE);
        }
        for (Integer key : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");
        System.out.println("Поставили статус DONE всем подзадачам задачи1.");
        for (Integer key : taskManager.getEpic(epicId1).getSubTasksID()) {
            taskManager.updateStatusSubTask(key, Status.DONE);
        }
        System.out.println(taskManager.getSubTask(subTaskId1));
        System.out.println(taskManager.getSubTask(subTaskId2));

        System.out.println("***********************************************************************************");
        System.out.println("Установили статус IN_PROGRESS всем подзадачаи задачи 2.");
        for (Integer key : taskManager.getEpic(epicId2).getSubTasksID()) {
            taskManager.updateStatusSubTask(key, Status.IN_PROGRESS);
        }
        System.out.println(taskManager.getSubTask(subTaskId3));
        System.out.println(taskManager.getSubTask(subTaskId4));
        System.out.println("***********************************************************************************");

        System.out.println("Список дел:");
        for (Integer key : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");
        System.out.println("Список задач:");
        for (Integer key : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : taskManager.getSubTasks().keySet()) {
            System.out.println(taskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Удаляем одну дела");
        taskManager.removeTask(task1ID);
        System.out.println("***********************************************************************************");

        System.out.println("Удаляем одну задачу");
        taskManager.removeEpic(epicId1);
        System.out.println("***********************************************************************************");

        System.out.println("Список дел:");
        for (Integer key : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список задач:");
        for (Integer key : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : taskManager.getSubTasks().keySet()) {
            System.out.println(taskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Очищаем список подзадач");
        taskManager.clearSubTasks();
        System.out.println("Список задач:");
        for (Integer key : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Очищаем список задач");
        taskManager.clearEpics();
        System.out.println("***********************************************************************************");

        System.out.println("Очищаем список дел");
        taskManager.clearTasks();
        System.out.println("***********************************************************************************");

        System.out.println("Список дел:");
        for (Integer key : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список задач:");
        for (Integer key : taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : taskManager.getSubTasks().keySet()) {
            System.out.println(taskManager.getSubTasks().get(key));
        }
    }
}