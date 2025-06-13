package service;

import model.task.*; // Ensure all necessary Task-related imports are here
import util.FileHandler;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManagerImpl implements TaskManager {

    private Set<Task> tasks = new TreeSet<>();

    public TaskManagerImpl() {
        // Initialize tasks by loading from file
        loadAllTasks();
    }

    public void loadAllTasks() {
        this.tasks = FileHandler.readTasksFromFile();
    }

    @Override
    public void displayAllTasks() {
        displayGivenTasks(tasks);
    }

    // GENERATE NEXT TASK ID
    private long generateNextTaskId() {
        // Find the maximum existing ID
        Optional<Long> maxIdOptional = tasks.stream()
                .map(task -> (long) task.getId())
                .max(Long::compare);

        long nextId;
        if (maxIdOptional.isPresent()) {
            long currentMaxId = maxIdOptional.get();
            // Check for potential overflow before incrementing
            if (currentMaxId >= Long.MAX_VALUE) {
                throw new IllegalStateException("Cannot generate new task ID: Maximum ID limit reached.");
            }
            nextId = currentMaxId + 1;
        } else {
            nextId = 1; // Start with 1 if no tasks exist
        }
        return nextId;
    }

    @Override
    public Task createTask(String title, String description, LocalDate dueDate, Priority priority, Status status, Category category, LocalDate creationDate) {
        long newTaskId = generateNextTaskId(); // Generate the ID here

        Task task = TaskCreator.createTask(
                newTaskId,
                title,
                description,
                dueDate,
                priority,
                status,
                category,
                creationDate
        );

        // Add the new task to the in-memory set
        boolean isAdded = tasks.add(task);

        if (isAdded) {
            //Write ALL tasks back to the file, overwriting old content
            FileHandler.writeTasksToFile(tasks); // Call the method that writes the whole Set
            System.out.println("Task created: " + task.getTitle() + " (ID: " + task.getId() + ") and saved to file.");
        } else {
            System.out.println("Task with Title " + task.getTitle() + " already exists or could not be added.");
            return null; // Task was not added
        }
        return task;
    }

    public void displayGivenTasks(Set<Task> tasksToDisplay) { // Renamed parameter for clarity
        System.out.println("\n--- Displaying Tasks ---");
        if (tasksToDisplay.isEmpty()) {
            System.out.println("No tasks to display.");
        } else {
            for (Task task : tasksToDisplay) {
                System.out.println("--- Task ID: " + task.getId() + " ---");
                System.out.println("Title: " + task.getTitle());
                System.out.println("Description: " + task.getDescription());
                System.out.println("Due Date: " + task.getDueDate());
                System.out.println("Priority: " + task.getPriority());
                System.out.println("Status: " + task.getStatus());
                System.out.println("Category: " + task.getCategory());
                System.out.println("Creation Date: " + task.getCreationDate());
                System.out.println();
            }
        }
    }

    @Override
    public Set<Task> searchTask(String title, String description) {
        final String searchTitle = (title != null) ? title : "";
        final String searchDescription = (description != null) ? description : "";

        Set<Task> foundTasks = tasks.stream()
                .filter(task -> (task.getTitle() != null && task.getTitle().toLowerCase().contains(searchTitle.toLowerCase())) &&
                        (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchDescription.toLowerCase()))
                )
                .collect(Collectors.toSet());

        return foundTasks;
    }

    @Override
    public void completeTask(Long taskIdToComplete) {
        if (taskIdToComplete == null) {
            System.out.println("Task ID to complete cannot be null.");
            return;
        }

        boolean taskFoundAndUpdated = false;
        // Iterate and find the task in the 'tasks' Set
        for (Task existingTask : tasks) {
            if (existingTask.getId() == taskIdToComplete) {
                existingTask.setStatus(Status.COMPLETED); // Modify the existing object
                taskFoundAndUpdated = true;
                break; // Found and updated, exit loop
            }
        }

        if (taskFoundAndUpdated) {
            // Write ALL tasks back to the file after modification
            FileHandler.writeTasksToFile(tasks);
            System.out.println("Task ID " + taskIdToComplete + " marked as COMPLETED and file saved.");
        } else {
            System.out.println("Task ID " + taskIdToComplete + " not found.");
        }
    }

    @Override
    public void deleteTask(Long taskIdToDelete) {
        if (taskIdToDelete == null) {
            System.out.println("Task ID to delete cannot be null.");
            return;
        }

        boolean removed = tasks.removeIf(task -> task.getId() == taskIdToDelete);

        if (removed) {
            // Write ALL tasks back to the file after modification
            FileHandler.writeTasksToFile(tasks);
            System.out.println("Task ID " + taskIdToDelete + " deleted and file saved.");
        } else {
            System.out.println("Task ID " + taskIdToDelete + " not found.");
        }
    }

    @Override
    public Task getTaskById(Long id) {
        if (id == null) return null;
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(tasks);
    }
}