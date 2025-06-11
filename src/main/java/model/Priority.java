package model;

public enum Priority {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    private final String priorityName;

    // The enum constructor
    // It must be private or package-private
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
