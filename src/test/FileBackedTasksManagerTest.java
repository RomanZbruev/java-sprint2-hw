import controllers.tracker.FileBackedTasksManager;
import model.tracker.Epic;
import model.tracker.Subtask;
import model.tracker.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import controllers.tracker.Status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;


public class FileBackedTasksManagerTest {
    FileBackedTasksManager fileBackedTasksManager;
    File file = new File("./src/storage/historyTest.csv");
    @BeforeEach
    void beforeEach(){
        try {
            Files.newBufferedWriter(file.toPath(),StandardOpenOption.TRUNCATE_EXISTING); // перед каждым запуском
            // очищаем файл истории
        }
        catch (IOException exception){
            System.out.println("Файл не прочитан");
        }
        fileBackedTasksManager = new FileBackedTasksManager(file);
    }

    @Test
    public void SaveAndLoadWhenEmptyListTask(){
        fileBackedTasksManager.save();
        try {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file.toPath())));
            br.readLine(); // читаем первую линию, с обозначением параметров
            Assertions.assertEquals("",br.readLine());
            br.close();
            } catch (FileNotFoundException ex) {
            System.out.println("Файл не существует");
        } catch (IOException ex) {
            System.out.println("Файл не прочитан");
        }
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertEquals(List.of(),fileBackedTasksManager1.getTaskList());
        Assertions.assertEquals(List.of(),fileBackedTasksManager1.getSubtaskList());
        Assertions.assertEquals(List.of(),fileBackedTasksManager1.getEpicList());
    }

    @Test
    public void saveAndLoadWhenEpicWithoutSubtasksListAndNonEmptyHistoryTest(){
        Epic epic = new Epic("t","d");
        fileBackedTasksManager.createNewEpic(epic);// при создании происходит запись
        fileBackedTasksManager.getEpicById(epic.getId()); //непустая история
        try {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file.toPath())));
            br.readLine(); // читаем первую линию, с обозначением параметров
            Assertions.assertEquals("1,EPIC,t,NEW,d,null,0,null",br.readLine());
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не существует");
        } catch (IOException ex) {
            System.out.println("Файл не прочитан");
        }
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        Epic expected = new Epic("t","d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected),fileBackedTasksManager1.getEpicList());
        Assertions.assertEquals(List.of(expected),fileBackedTasksManager1.getHistoryManager().getHistory());
    }

    @Test
    public void saveAndLoadStandardWorkWhenEpicWithSubtaskListAndNonEmptyHistoryTest(){
        Epic epic = new Epic("t","d");
        Subtask subtask = new Subtask("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        fileBackedTasksManager.createNewEpic(epic);// при создании происходит запись
        fileBackedTasksManager.getEpicById(epic.getId()); //непустая история
        fileBackedTasksManager.createNewSubtask(subtask,epic.getId());
        try {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file.toPath())));
            br.readLine(); // читаем первую линию, с обозначением параметров
            Assertions.assertEquals("1,EPIC,t,NEW,d,2000-01-01T01:01,1,2000-01-01T01:02",br.readLine());
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не существует");
        } catch (IOException ex) {
            System.out.println("Файл не прочитан");
        }
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        Epic expected = new Epic("t","d");
        expected.setId(1);
        expected.setStatus(Status.NEW);
        expected.setStartTime(LocalDateTime.of(2000,1,1,1,1));
        expected.setDuration(1);
        expected.setEndTime((LocalDateTime.of(2000,1,1,1,2)));
        Assertions.assertEquals(List.of(expected),fileBackedTasksManager1.getEpicList());
        Assertions.assertEquals(List.of(expected),fileBackedTasksManager1.getHistoryManager().getHistory());
    }

    @Test
    public void saveAndLoadWithTaskAndEmptyHistoryTest(){
        Task task = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        fileBackedTasksManager.createNewTask(task);// при создании происходит запись
        try {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file.toPath())));
            br.readLine(); // читаем первую линию, с обозначением параметров
            Assertions.assertEquals("1,TASK,t,NEW,d,2000-01-01T01:01,1,2000-01-01T01:02",br.readLine());
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не существует");
        } catch (IOException ex) {
            System.out.println("Файл не прочитан");
        }
        FileBackedTasksManager fileBackedTasksManager1 = FileBackedTasksManager.loadFromFile(file);
        Task expected = new Task("t","d",1,
                LocalDateTime.of(2000,1,1,1,1));
        expected.setId(1);
        expected.setStatus(Status.NEW);
        Assertions.assertEquals(List.of(expected),fileBackedTasksManager1.getTaskList());
        Assertions.assertEquals(List.of(),fileBackedTasksManager1.getHistoryManager().getHistory());
    }
}
