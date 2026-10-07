package gr.uom.strategicplanning.controllers.entities;

/** Result of deleting (soft or hard) or restoring a KPI or metric. */
public class DeletionResult {
    public enum Mode { SOFT, HARD, RESTORED }

    private Long id;
    private String name;
    private Mode mode;
    /** Number of history values (reports) removed - only non-zero for hard deletes. */
    private long removedReports;

    public DeletionResult() {
    }

    public DeletionResult(Long id, String name, Mode mode, long removedReports) {
        this.id = id;
        this.name = name;
        this.mode = mode;
        this.removedReports = removedReports;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Mode getMode() {
        return mode;
    }

    public long getRemovedReports() {
        return removedReports;
    }
}
