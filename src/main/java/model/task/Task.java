package model.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Task implements Comparable<Task> {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Status status;
    private Category category;
    private LocalDate creationDate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor
    public Task(long id, String title, String description, LocalDate dueDate, Priority priority, Status status, Category category, LocalDate creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.category = category;
        this.creationDate = creationDate;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "ID:" + id + "\n" +
                "Title:" + title + "\n" +
                "Description:" + description + "\n" +
                "Due Date:" + dueDate.format(DATE_FORMATTER) + "\n" +
                "Priority:" + priority.getPriorityName() + "\n" +
                "Status:" + status.getStatusName() + "\n" +
                "Category:" + category.name() + "\n" +
                "Creation Date:" + creationDate.format(DATE_FORMATTER);
    }

    @Override
    public int compareTo(Task task) {
        return this.title.compareTo(task.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
