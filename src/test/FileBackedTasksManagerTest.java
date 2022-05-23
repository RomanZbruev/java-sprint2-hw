import controllers.tracker.FileBackedTasksManager;
import model.tracker.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import controllers.tracker.Status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
            Assertions.assertEquals("1,EPIC,t,NEW,d",br.readLine());
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
    public void saveAndLoadWithTaskAndEmptyHistoryTest(){
        Epic epic = new Epic("t","d");
        fileBackedTasksManager.createNewEpic(epic);// при создании происходит запись
        try {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file.toPath())));
            br.readLine(); // читаем первую линию, с обозначением параметров
            Assertions.assertEquals("1,EPIC,t,NEW,d",br.readLine());
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
        Assertions.assertEquals(List.of(),fileBackedTasksManager1.getHistoryManager().getHistory());
    }
}
