package model.task;

import java.time.LocalDate;

public class TaskCreator {

    public static Task createTask(long id, String title, String description, LocalDate dueDate, Priority priority, Status status,Category category, LocalDate creationDate) {
        if (category== Category.WORK){
            return new WorkTask(id, title, description, dueDate, priority, status,category, creationDate);
        } else  {
            return new PersonalTask(id, title, description, dueDate, priority, status,category, creationDate);
        }
    }
}
