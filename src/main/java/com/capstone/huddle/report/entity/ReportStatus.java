package com.capstone.huddle.report.entity;


public enum ReportStatus {

    PENDING("Pending"),
    REVIEWED("Reviewed"),
    RESOLVED("Resolved"),
    REJECTED("Rejected");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReportStatus fromDisplayName(String displayName) {
        for (ReportStatus status : ReportStatus.values()) {
            if (status.displayName.equalsIgnoreCase(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No status found for display name: " + displayName);
    }
}
