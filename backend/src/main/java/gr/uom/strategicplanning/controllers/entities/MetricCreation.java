package gr.uom.strategicplanning.controllers.entities;


import java.util.List;

public class MetricCreation {
    private String name;
    private String equation;

    public MetricCreation(String name, String equation, List<String> indicatorList) {
        this.name = name;
        this.equation = equation;
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

}
