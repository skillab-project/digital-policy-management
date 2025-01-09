package gr.uom.strategicplanning.models.euproject;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Organization {
    @Id
    private Long id;
    private String name;
    private String acronym;
    private String country;
    private String street;
    private String city;
    private String postCode;
    private String organizationUrl;
    private String vatNumber;
    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "coordinator")
    @JsonIgnore
    private List<Project> coordinations;
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Project> projects;


    public Organization() {
    }

    public Organization(Long id, String name, String acrony, String country, String street, String city,
                        String postCode, String organizationUrl, String vatNumber) {
        this.id = id;
        this.name = name;
        this.acronym = acrony;
        this.country = country;
        this.street = street;
        this.city = city;
        this.postCode = postCode;
        this.organizationUrl = organizationUrl;
        this.vatNumber = vatNumber;

        this.coordinations = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public void addCoordination(Project project){
        coordinations.add(project);
    }

    public void addProjects(Project project){
        projects.add(project);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public void setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public List<Project> getCoordinations() {
        return coordinations;
    }

    public void setCoordinations(List<Project> coordinations) {
        this.coordinations = coordinations;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", acronym='" + acronym + '\'' +
                ", country='" + country + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", postCode='" + postCode + '\'' +
                ", organizationUrl='" + organizationUrl + '\'' +
                ", vatNumber='" + vatNumber + '\'' +
                '}';
    }
}
