package model.user;

import model.task.Category;
import model.task.Priority;
import model.task.Status;
import model.task.Task;
import service.TaskManagerImpl;

import java.time.LocalDate;
import java.util.Set;


public abstract class User implements Comparable<User> {

    private TaskManagerImpl taskManager;
    private String username;
    private String password;

    public User(String username, String password, TaskManagerImpl taskManager) {
        this.username = username;
        this.password = password;
        this.taskManager = taskManager;
    }


    public Task createTask(String title, String description, LocalDate dueDate, Priority priority, Status status, Category category, LocalDate creationDate) {
        return taskManager.createTask(title, description, dueDate, priority, status, category, creationDate);
    }

    public void displayAllTasks() {

        taskManager.displayAllTasks();
    }

    public void displayGivenTasks(Set<Task> tasks) {

        taskManager.displayGivenTasks(tasks);
    }

    public Set<Task> searchTask(String title, String description) {

        return taskManager.searchTask(title, description);
    }

    public void completeTask(Long completedTaskId) {
        taskManager.completeTask(completedTaskId);

    }

    public void deleteTask(Long deletedTaskId) {
        taskManager.deleteTask(deletedTaskId);
    }

    @Override
    public int compareTo(User user) {
        return this.username.compareTo(user.username);
    }

    public TaskManagerImpl getTaskManager() {
        return taskManager;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
