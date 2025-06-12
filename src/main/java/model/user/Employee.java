package model.user;

import model.task.*;
import service.TaskManagerImpl;

public class Employee extends User {
    public Employee(String username, String password, TaskManagerImpl taskManager) {
        super(username, password, taskManager);
    }
    @Override
    public void completeTask(Long completedTaskId) {
        Task completedTask = getTaskManager().getTaskById(completedTaskId);
        if(completedTask instanceof PersonalTask) {
            getTaskManager().completeTask(completedTaskId);
        }else if(completedTask instanceof WorkTask) {
            System.out.println("you have no permission to complete this work task with Id ->" + completedTask.getId() + "<- and title ->" + completedTask.getTitle()+ "<-");
        }
    }
}
