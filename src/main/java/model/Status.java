package model;

public enum Status {
    PENDING("PENDING"),
    COMPLETED("COMPLETED");
    private final String statusName;

    // The enum constructor
    // It must be private or package-private
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
