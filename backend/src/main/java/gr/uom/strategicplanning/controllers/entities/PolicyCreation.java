package gr.uom.strategicplanning.controllers.entities;


public class PolicyCreation {
    private String name;
    private String description;
    private String sector;
    private String region;

    public PolicyCreation(String name, String description, String sector, String region) {
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
