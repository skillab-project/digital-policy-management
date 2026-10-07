package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Indicator {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String symbol;
    @ManyToMany(mappedBy = "indicatorList")
    @JsonIgnore
    private List<Kpi> kpiList;
    // Soft-delete flag (see Kpi#deleted for why it is a nullable wrapper with a DB default)
    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;
    private Date deletedAt;

    public Indicator() {
    }

    public Indicator(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public void addKpi(Kpi Kpi){
        this.kpiList.add(Kpi);
    }

    public void removeKpi(Kpi kpi){
        if (this.kpiList != null) {
            this.kpiList.remove(kpi);
        }
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<Kpi> getKpiList() {
        return kpiList;
    }

    public void setKpiList(List<Kpi> kpiList) {
        this.kpiList = kpiList;
    }
}
