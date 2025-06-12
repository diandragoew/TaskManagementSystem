package model.task;

import java.time.LocalDate;

public class PersonalTask extends Task  {

     PersonalTask(long id, String title, String description, LocalDate dueDate, Priority priority, Status status,Category category, LocalDate creationDate) {
        super(id, title, description, dueDate, priority, status,category, creationDate);
    }
}
