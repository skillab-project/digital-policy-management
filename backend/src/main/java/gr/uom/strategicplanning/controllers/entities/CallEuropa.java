package gr.uom.strategicplanning.controllers.entities;

public class CallEuropa {
    private String title;
    private String description;
    private String openingDate;
    private String deadlineDate;
    private String callIdentifier;
    private String typesOfAction;

    public CallEuropa(String title, String description, String openingDate, String deadlineDate, String callIdentifier, String typesOfAction) {
        this.title = title;
        this.description = description;
        this.openingDate = openingDate;
        this.deadlineDate = deadlineDate;
        this.callIdentifier = callIdentifier;
        this.typesOfAction = typesOfAction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getCallIdentifier() {
        return callIdentifier;
    }

    public void setCallIdentifier(String callIdentifier) {
        this.callIdentifier = callIdentifier;
    }

    public String getTypesOfAction() {
        return typesOfAction;
    }

    public void setTypesOfAction(String typesOfAction) {
        this.typesOfAction = typesOfAction;
    }
}
