package model.task;

import java.time.LocalDate;

public class WorkTask extends Task {
     WorkTask(long id, String title, String description, LocalDate dueDate, Priority priority, Status status,Category category, LocalDate creationDate) {
        super(id, title, description, dueDate, priority, status,category, creationDate);
    }
}
