package service;

import model.Status;
import model.Task;
import util.FileHandler;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TaskManagerImpl implements TaskManager {

    private  Set<Task> tasks = new TreeSet<>();

   public  void loadAllTasks(){
        tasks = FileHandler.readTasksFromFile();
    }
    @Override
    public  void createTasks(Set<Task> tasks) {

        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                //if isAdded is true, it means that the task is added in the collection and it will be written to the file as well
                boolean isAdded = tasks.add(task);
                if (isAdded) {
                    FileHandler.writeTasksToFile(task, true);
                }
            }
        } else {
            System.out.println("No tasks added.");
        }
    }

    @Override
    public void displayAllTasks() {


        // --- Example of reading tasks ---
        Set<Task> loadedTasks = FileHandler.readTasksFromFile();
        displayGivenTasks(loadedTasks);
    }

    void displayGivenTasks(Set<Task> tasks) {
        System.out.println("\nLoaded Tasks:");
        if (tasks.isEmpty()) {
            System.out.println("No tasks loaded or file is empty.");
        } else {
            for (Task task : tasks) {
                System.out.println("--- Task ID: " + task.getId() + " ---");
                System.out.println("Title: " + task.getTitle());
                System.out.println("Description: " + task.getDescription());
                System.out.println("Due Date: " + task.getDueDate());
                System.out.println("Priority: " + task.getPriority());
                System.out.println("Status: " + task.getStatus());
                System.out.println("Creation Date: " + task.getCreationDate());
                System.out.println();
            }
        }
    }

    @Override
    public Set<Task> searchTask(String title, String description) {
        Set<Task> findedTasks = tasks.stream().filter(task -> task.getTitle().contains(title) || task.getDescription().contains(description)).collect(Collectors.toSet());
        return findedTasks;
    }

    @Override
    public void completeTasks(Set<Task> completedTasks) {
        if (!completedTasks.isEmpty()) {
            for (Task completedtask : completedTasks) {
                tasks.stream().filter(task -> task.getId() == completedtask.getId()).findFirst().ifPresent(task -> task.setStatus(Status.COMPLETED));
            }
            for (Task task : tasks) {
                FileHandler.writeTasksToFile(task, false);
            }
        }
    }

    @Override
    public void deleteTasks(List<Task> deletedTasks) {
        if (!deletedTasks.isEmpty()) {
            for (Task deletedtask : deletedTasks) {
                tasks.removeIf(task -> task.getId() == deletedtask.getId());
            }
            for (Task task : tasks) {
                FileHandler.writeTasksToFile(task, false);
            }
        }
    }

}
