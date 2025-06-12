package model.task;

public enum Status {
    PENDING("PENDING"),
    COMPLETED("COMPLETED");
    private final String statusName;

    Status(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    @Override
    public String toString() {
        return statusName;
    }
}
