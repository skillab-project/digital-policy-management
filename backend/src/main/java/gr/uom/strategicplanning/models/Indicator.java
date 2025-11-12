package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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

    public Indicator() {
    }

    public Indicator(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public void addKpi(Kpi Kpi){
        this.kpiList.add(Kpi);
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
