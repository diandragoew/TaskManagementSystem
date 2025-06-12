package model.user;

import service.TaskManagerImpl;

public class Administrator extends User {
    public Administrator(String username, String password, TaskManagerImpl taskManager) {
        super(username, password, taskManager);
    }
}
