package util;

import model.task.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandler {

    private static final String TASKS_FILE_PATH = "src/main/resources/tasks";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Set<Task> readTasksFromFile() {
        Set<Task> tasks = new TreeSet<>(); // Initialize a new Set for each read operation
        Path filePath = Paths.get(TASKS_FILE_PATH);

        if (!Files.exists(filePath)) {
            System.out.println("Tasks file not found at: " + TASKS_FILE_PATH);
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            StringBuilder currentTaskBlock = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("task") && currentTaskBlock.length() > 0) {
                    Task task = parseTaskBlock(currentTaskBlock.toString());
                    if (task != null) {
                        tasks.add(task);
                    }
                    currentTaskBlock.setLength(0); // Reset for new task
                }
                currentTaskBlock.append(line).append("\n"); // Append line to current block
            }
            if (currentTaskBlock.length() > 0) {
                Task task = parseTaskBlock(currentTaskBlock.toString());
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading tasks from file: " + e.getMessage());
        }
        return tasks;
    }

    private static Task parseTaskBlock(String block) {
        int id = -1;
        String title = null;
        String description = null;
        LocalDate dueDate = null;
        Priority priority = null;
        Status status = null;
        Category category = null;
        LocalDate creationDate = null;

        Pattern idPattern = Pattern.compile("ID:(\\d+)");
        Pattern titlePattern = Pattern.compile("Title:(.*)");
        Pattern descPattern = Pattern.compile("Description:(.*)");
        Pattern dueDatePattern = Pattern.compile("Due Date:(\\d{4}-\\d{2}-\\d{2})");
        Pattern priorityPattern = Pattern.compile("Priority:(\\w+)");
        Pattern statusPattern = Pattern.compile("Status:(\\w+)");
        Pattern categoryPattern = Pattern.compile("Category:(\\w+)");
        Pattern creationDatePattern = Pattern.compile("Creation Date:(\\d{4}-\\d{2}-\\d{2})");

        Matcher matcher;

        matcher = idPattern.matcher(block);
        if (matcher.find()) {
            id = Integer.parseInt(matcher.group(1));
        }

        matcher = titlePattern.matcher(block);
        if (matcher.find()) {
            title = matcher.group(1).trim();
        }

        matcher = descPattern.matcher(block);
        if (matcher.find()) {
            description = matcher.group(1).trim();
        }

        matcher = dueDatePattern.matcher(block);
        if (matcher.find()) {
            dueDate = LocalDate.parse(matcher.group(1), DATE_FORMATTER);
        }

        matcher = priorityPattern.matcher(block);
        if (matcher.find()) {
            try {
                priority = Priority.valueOf(matcher.group(1).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid Priority: " + matcher.group(1));
            }
        }

        matcher = statusPattern.matcher(block);
        if (matcher.find()) {
            try {
                status = Status.valueOf(matcher.group(1).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid Status: " + matcher.group(1));
            }
        }

        matcher = categoryPattern.matcher(block);
        if (matcher.find()) {
            try {
                category = Category.valueOf(matcher.group(1).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid Category: " + matcher.group(1));
            }
        }

        matcher = creationDatePattern.matcher(block);
        if (matcher.find()) {
            creationDate = LocalDate.parse(matcher.group(1), DATE_FORMATTER);
        }

        if (id != -1 && title != null && description != null && dueDate != null &&
                priority != null && status != null && category != null && creationDate != null) {
            return TaskCreator.createTask(id, title, description, dueDate, priority, status, category, creationDate);
        } else {
            System.err.println("Could not fully parse task block:\n" + block);
            return null;
        }
    }


    public static void writeTasksToFile(Set<Task> tasks) {
        Path filePath = Paths.get(TASKS_FILE_PATH);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            for (Task task : tasks) {
                writer.write("task");
                writer.newLine();
                writer.write(task.toString()); // Uses Task's toString()
                writer.newLine(); // Newline after task block
                writer.newLine(); // Empty line between tasks
            }
            System.out.println("Tasks written successfully to: " + TASKS_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error writing tasks to file: " + e.getMessage());
        }
    }
}