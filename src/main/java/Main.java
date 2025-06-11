import model.Priority;
import model.Status;
import model.Task;
import service.TaskManagerImpl;
import util.FileHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        TaskManagerImpl taskManager = new TaskManagerImpl();
        //1
        taskManager.loadAllTasks();

        //2.
        Set<Task> tasks = new TreeSet<>();
        // --- Example of writing tasks ---
        tasks.add(new Task(1, "first Task", "interesting task", LocalDate.of(2021, 2, 20), Priority.HIGH, Status.PENDING, LocalDate.of(2020, 1, 1))); //
        tasks.add(new Task(2, "second Task", "very difficult task", LocalDate.of(2022, 3, 15), Priority.MEDIUM, Status.COMPLETED, LocalDate.of(2021, 1, 10)));
        tasks.add(new Task(3, "third Task", "easy task", LocalDate.of(2023, 7, 1), Priority.LOW, Status.PENDING, LocalDate.of(2022, 5, 20)));
        taskManager.createTasks(tasks);

        //3
        Set<Task> searchedTasks = taskManager.searchTask("first Task", "interesting task");

        //4
        taskManager.completeTasks(searchedTasks);

        //5

    }
}
