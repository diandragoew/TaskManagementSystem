package service;

import model.task.Category;
import model.task.Priority;
import model.task.Status;
import model.task.Task;

import java.time.LocalDate;
import java.util.Set;

public interface TaskManager {
    //return new created task ID
    Task createTask(String title, String description, LocalDate dueDate, Priority priority, Status status, Category category, LocalDate creationDate);

    void displayAllTasks();
    Set<Task> searchTask(String title, String description);

    void completeTask(Long taskIdToComplete);

    void deleteTask(Long completedTaskId);

    Task getTaskById(Long id);
}
