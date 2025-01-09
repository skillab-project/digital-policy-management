package gr.uom.strategicplanning.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Metric {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String equation;
    @ManyToMany
    @JoinTable(name="metric_indicator",
            joinColumns = @JoinColumn(name="metric_name"),
            inverseJoinColumns = @JoinColumn(name="indicator_name"))
    private List<Indicator> indicatorList;

    public Metric() {
    }

    public Metric(String name, String equation) {
        this.name = name;
        this.equation = equation;
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

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public List<Indicator> getIndicatorList() {
        return indicatorList;
    }

    public void setIndicatorList(List<Indicator> indicatorList) {
        this.indicatorList = indicatorList;
    }
}
