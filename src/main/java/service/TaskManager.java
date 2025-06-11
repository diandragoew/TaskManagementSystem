package service;

import model.Priority;
import model.Status;
import model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    //return new created task ID
    void createTasks(Set<Task> tasks);

    void displayAllTasks();
    Set<Task> searchTask(String title, String description);

    void completeTasks(Set<Task> completedTasks);

    void deleteTasks(List<Task> deletedTasks);

}
