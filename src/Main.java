import main.service.impl.InMemoryTaskManager;
import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import main.model.Task;
import main.service.impl.Managers;

public class Main {

    public static void main(String[] args) throws Exception {

        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

        Task task1 = new Task("Съездить к родителям", "В выходные съездить в гости к родителям");
        inMemoryTaskManager.createTask(task1);
        int task1ID = task1.getId();
        System.out.println("Таск №1 добавлен");

        System.out.println("***********************************************************************************");

        Task task2 = new Task("Сделать потолки", "Установить натяжные потолки в квартире");
        inMemoryTaskManager.createTask(task2);
        int task2ID = task2.getId();
        System.out.println("Таск №2 добавлен");

        System.out.println("***********************************************************************************");
        System.out.println("***********************************************************************************");

        Epic epic1 = new Epic("Съездить за покупками", "Закупиться на месяц");
        inMemoryTaskManager.createEpic(epic1);
        int epicId1 = epic1.getId();
        System.out.println("Пустой эпик №1 добавлен");
        System.out.println("***********************************************************************************");

        Epic epic2 = new Epic("ТО машины", "Отвезти машину в сервис для прохождении ТО");
        inMemoryTaskManager.createEpic(epic2);
        int epicId2 = epic2.getId();
        System.out.println("Пустой эпик №2 добавлен");
        System.out.println("***********************************************************************************");
        System.out.println("***********************************************************************************");

        Subtask subtask1 = new Subtask("Продуктовый магазин", "Молоко, сыр, хлеб, йогурты", epicId1);
        inMemoryTaskManager.createSubtasks(subtask1);
        int subTaskId1 = subtask1.getId();
        System.out.println("Сабтаск №1 к эпику №1 добавлен");
        System.out.println("***********************************************************************************");

        Subtask subtask2 = new Subtask("Хозтовары", "Порошок, мыло, гель для душа", epicId1);
        inMemoryTaskManager.createSubtasks(subtask2);
        int subTaskId2 = subtask2.getId();
        System.out.println("Сабтаск №2 к эпику №1 добавлен");
        System.out.println("***********************************************************************************");

        Subtask subtask3 = new Subtask("Замена масло", "Было налито масло Лукойл", epicId2);
        inMemoryTaskManager.createSubtasks(subtask3);
        int subTaskId3 = subtask3.getId();
        System.out.println("Сабтаск №3 к эпику №1 добавлен");
        System.out.println("***********************************************************************************");

        Subtask subtask4 = new Subtask("Замена фильтра", "Заменили масляной фильтр", epicId2);
        inMemoryTaskManager.createSubtasks(subtask4);
        int subTaskId4 = subtask4.getId();
        System.out.println("***********************************************************************************");

        System.out.println("Поставили статус DONE всем делам.");
        for (Integer key : inMemoryTaskManager.getTasks().keySet()){
            inMemoryTaskManager.getTask(key).setStatus(Status.DONE);
        }
        for(Integer key : inMemoryTaskManager.getEpic(epicId1).getSubTasksID()){
            inMemoryTaskManager.updateStatusSubTask(key, Status.DONE);
        }
        System.out.println("***********************************************************************************");
        System.out.println("Таски1 таск2 епик1");
        inMemoryTaskManager.getTask(task1ID);
        inMemoryTaskManager.getTask(task2ID);
        inMemoryTaskManager.getEpic(epicId1);

        System.out.println(inMemoryTaskManager.getHistory().getHistory());
        System.out.println("***********************************************************************************");
        System.out.println("Епики1 таск2");
        inMemoryTaskManager.getEpic(epicId1);
        inMemoryTaskManager.getTask(task2ID);
        System.out.println(inMemoryTaskManager.getHistory().getHistory());
        System.out.println("***********************************************************************************");
        System.out.println("Сабтаски");
        inMemoryTaskManager.getSubTask(subTaskId1);
        inMemoryTaskManager.getSubTask(subTaskId2);
        inMemoryTaskManager.getSubTask(subTaskId3);

        System.out.println(inMemoryTaskManager.getHistory().getHistory());
        System.out.println("***********************************************************************************");
        System.out.println("Удалили сабтаски под айди №1 и №3");
        inMemoryTaskManager.removeSubTask(subTaskId1);
        inMemoryTaskManager.removeSubTask(subTaskId3);
        System.out.println(inMemoryTaskManager.getHistory().getHistory());
        System.out.println("***********************************************************************************");
        System.out.println("Удалили epic под айди №1");
        inMemoryTaskManager.removeEpic(epicId1);
        System.out.println(inMemoryTaskManager.getHistory().getHistory());
        System.out.println("***********************************************************************************");
    }
}