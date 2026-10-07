package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Policy {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String sector;
    private String region;
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kpi> kpiList;

    public Policy() {
    }

    public Policy(String name, String description, String sector, String region) {
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.region = region;
        this.kpiList = new ArrayList<>();
    }

    public void addKpi(Kpi kpi){
        this.kpiList.add(kpi);
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

    /**
     * Active (not soft-deleted) KPIs of this policy. This is what the API returns as "kpiList".
     * Hibernate uses field access here, so filtering in the getter does not affect persistence.
     */
    public List<Kpi> getKpiList() {
        if (kpiList == null) {
            return null;
        }
        return kpiList.stream().filter(k -> !k.isDeleted()).collect(Collectors.toList());
    }

    /** All KPIs of this policy, including soft-deleted ones. Internal use only. */
    @JsonIgnore
    public List<Kpi> getAllKpisIncludingDeleted() {
        return kpiList;
    }

    public void setKpiList(List<Kpi> kpiList) {
        this.kpiList = kpiList;
    }
}
