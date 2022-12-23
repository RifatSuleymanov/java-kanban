import service.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) throws Exception {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task("Съездить к родителям", "В выходные съездить в гости к родителям");
        inMemoryTaskManager.createTask(task1);
        int task1ID = task1.getId();

        Task task2 = new Task("Сделать потолки", "Установить натяжные потолки в квартире");
        inMemoryTaskManager.createTask(task2);

        Epic epic1 = new Epic("Съездить за покупками", "Закупиться на месяц");
        inMemoryTaskManager.createEpic(epic1);
        int epicId1 = epic1.getId();

        Epic epic2 = new Epic("ТО машины", "Отвезти машину в сервис для прохождении ТО");
        inMemoryTaskManager.createEpic(epic2);
        int epicId2 = epic2.getId();

        Subtask subtask1 = new Subtask("Продуктовый магазин", "Молоко, сыр, хлеб, йогурты", epicId1);
        inMemoryTaskManager.createSubtasks(subtask1);
        int subTaskId1 = subtask1.getId();

        Subtask subtask2 = new Subtask("Хозтовары", "Порошок, мыло, гель для душа", epicId1);
        inMemoryTaskManager.createSubtasks(subtask2);
        int subTaskId2 = subtask2.getId();

        Subtask subtask3 = new Subtask("Замена масло", "Было налито масло Лукойл", epicId2);
        inMemoryTaskManager.createSubtasks(subtask3);
        int subTaskId3 = subtask3.getId();

        Subtask subtask4 = new Subtask("Замена фильтра", "Заменили масляной фильтр", epicId2);
        inMemoryTaskManager.createSubtasks(subtask4);
        int subTaskId4 = subtask4.getId();

        System.out.println("Список дел:");
        for (Integer key : inMemoryTaskManager.getTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список задач:");
        for (Integer key : inMemoryTaskManager.getEpics().keySet()) {
            System.out.println(inMemoryTaskManager.getEpics().get(key));
        }

        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : inMemoryTaskManager.getSubTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Получение одного дела по ID:");
        System.out.println(inMemoryTaskManager.getTask(task1ID));
        System.out.println("***********************************************************************************");

        System.out.println("Получение одной задачи по ID:");
        System.out.println(inMemoryTaskManager.getEpic(epicId1));
        System.out.println("***********************************************************************************");

        System.out.println("Получение одной подзадачи по ID:");
        System.out.println(inMemoryTaskManager.getSubTask(subTaskId1));
        System.out.println("***********************************************************************************");

        System.out.println("Поставили статус DONE всем делам.");
        for (Integer key : inMemoryTaskManager.getTasks().keySet()) {
            inMemoryTaskManager.getTask(key).setTaskStatus(Status.DONE);
        }
        for (Integer key : inMemoryTaskManager.getTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");
        System.out.println("Поставили статус DONE всем подзадачам задачи1.");
        for (Integer key : inMemoryTaskManager.getEpic(epicId1).getSubTasksID()) {
            inMemoryTaskManager.updateStatusSubTask(key, Status.DONE);
        }
        System.out.println(inMemoryTaskManager.getSubTask(subTaskId1));
        System.out.println(inMemoryTaskManager.getSubTask(subTaskId2));

        System.out.println("***********************************************************************************");
        System.out.println("Установили статус IN_PROGRESS всем подзадачаи задачи 2.");
        for (Integer key : inMemoryTaskManager.getEpic(epicId2).getSubTasksID()) {
            inMemoryTaskManager.updateStatusSubTask(key, Status.IN_PROGRESS);
        }
        System.out.println(inMemoryTaskManager.getSubTask(subTaskId3));
        System.out.println(inMemoryTaskManager.getSubTask(subTaskId4));
        System.out.println("***********************************************************************************");

        System.out.println("Список дел:");
        for (Integer key : inMemoryTaskManager.getTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getTasks().get(key));
        }

        System.out.println("***********************************************************************************");
        System.out.println("Список задач:");
        for (Integer key : inMemoryTaskManager.getEpics().keySet()) {
            System.out.println(inMemoryTaskManager.getEpics().get(key));
        }
        System.out.println("Просмотр истории: ");
        for (int i = 0; i < inMemoryTaskManager.getHistory().getRequestHistory().size(); i++){
            System.out.println("#" + (i + 1) + "-id " + inMemoryTaskManager.getHistory().getRequestHistory().get(i).getId());
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : inMemoryTaskManager.getSubTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Удаляем одну дела");
        inMemoryTaskManager.removeTask(task1ID);
        System.out.println("***********************************************************************************");

        System.out.println("Удаляем одну задачу");
        inMemoryTaskManager.removeEpic(epicId1);
        System.out.println("***********************************************************************************");

        System.out.println("Список дел:");
        for (Integer key : inMemoryTaskManager.getTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список задач:");
        for (Integer key : inMemoryTaskManager.getEpics().keySet()) {
            System.out.println(inMemoryTaskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : inMemoryTaskManager.getSubTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Очищаем список подзадач");
        inMemoryTaskManager.clearSubTasks();
        System.out.println("Список задач:");
        for (Integer key : inMemoryTaskManager.getEpics().keySet()) {
            System.out.println(inMemoryTaskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Очищаем список задач");
        inMemoryTaskManager.clearEpics();
        System.out.println("***********************************************************************************");
        System.out.println("Просмотр истории: ");
        for (int i = 0; i < inMemoryTaskManager.getHistory().getRequestHistory().size(); i++){
            System.out.println("#" + (i + 1) + "-id " + inMemoryTaskManager.getHistory().getRequestHistory().get(i).getId());
        }

        System.out.println("Очищаем список дел");
        inMemoryTaskManager.clearTasks();
        System.out.println("***********************************************************************************");

        System.out.println("Список дел:");
        for (Integer key : inMemoryTaskManager.getTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список задач:");
        for (Integer key : inMemoryTaskManager.getEpics().keySet()) {
            System.out.println(inMemoryTaskManager.getEpics().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Список подзадач:");
        for (Integer key : inMemoryTaskManager.getSubTasks().keySet()) {
            System.out.println(inMemoryTaskManager.getSubTasks().get(key));
        }
        System.out.println("***********************************************************************************");

        System.out.println("Просмотр истории: ");
        for (int i = 0; i < inMemoryTaskManager.getHistory().getRequestHistory().size(); i++){
            System.out.println("#" + (i + 1) + "-id " + inMemoryTaskManager.getHistory().getRequestHistory().get(i).getId());
        }
        System.out.println("***********************************************************************************");
    }
}