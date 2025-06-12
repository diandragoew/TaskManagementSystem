package model.task;

public enum Priority {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String priorityName;

    Priority(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getPriorityName() {
        return priorityName;
    }

    @Override
    public String toString() {
        return priorityName;
    }
}
