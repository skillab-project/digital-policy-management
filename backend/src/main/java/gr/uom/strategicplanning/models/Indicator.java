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
    private List<Metric> metricList;

    public Indicator() {
    }

    public Indicator(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public void addMetric(Metric Metric){
        this.metricList.add(Metric);
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

    public List<Metric> getMetricList() {
        return metricList;
    }

    public void setMetricList(List<Metric> metricList) {
        this.metricList = metricList;
    }
}
