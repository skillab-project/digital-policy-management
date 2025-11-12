package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Double targetValue;
    private String targetTime;
    @ManyToOne
    @JoinColumn(name = "policy_id")
    @JsonIgnore
    private Policy policy;

    public Metric() {
    }

    public Metric(String name, String equation) {
        this.name = name;
        this.equation = equation;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public String getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(String targetTime) {
        this.targetTime = targetTime;
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
