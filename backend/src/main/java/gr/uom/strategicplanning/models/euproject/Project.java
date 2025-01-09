package gr.uom.strategicplanning.models.euproject;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Project {
    @Id
    private Long id;
    private String acronym;
    private String title;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd")
    private Date endDate;
    @Column(
            columnDefinition = "TEXT")
    private String objective;
    private String totalCost;
    private String programme;
    private String topics;
    private String frameworkProgramme;
    private String call;
    private String fundingScheme;
    @ManyToOne
    private Organization coordinator;
    @ManyToMany
    @JoinTable(
            name="projects_participants",
            joinColumns = @JoinColumn(name="project_name"),
            inverseJoinColumns = @JoinColumn(name="organization_name"))
    private List<Organization> participants;


    public Project() {
    }

    public Project(Long id, String acrony, String title, Date startDate, Date endDate, String objective,
                   String totalCost, String programme, String topics, String frameworkProgramme, String call,
                   String fundingScheme) {
        this.id = id;
        this.acronym = acrony;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.objective = objective;
        this.totalCost = totalCost;
        this.programme = programme;
        this.topics = topics;
        this.frameworkProgramme = frameworkProgramme;
        this.call = call;
        this.fundingScheme = fundingScheme;

        this.participants = new ArrayList<>();
    }

    public void addParticipants(Organization organization){
        participants.add(organization);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getFrameworkProgramme() {
        return frameworkProgramme;
    }

    public void setFrameworkProgramme(String frameworkProgramme) {
        this.frameworkProgramme = frameworkProgramme;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getFundingScheme() {
        return fundingScheme;
    }

    public void setFundingScheme(String fundingScheme) {
        this.fundingScheme = fundingScheme;
    }

    public Organization getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Organization coordinator) {
        this.coordinator = coordinator;
    }

    public List<Organization> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Organization> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", acronym='" + acronym + '\'' +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", objective='" + objective + '\'' +
                ", totalCost='" + totalCost + '\'' +
                ", programme='" + programme + '\'' +
                ", topics='" + topics + '\'' +
                ", frameworkProgramme='" + frameworkProgramme + '\'' +
                ", call='" + call + '\'' +
                ", fundingScheme='" + fundingScheme + '\'' +
//                ", coordinator=" + coordinator +
//                ", participants=" + participants +
                '}';
    }
}
