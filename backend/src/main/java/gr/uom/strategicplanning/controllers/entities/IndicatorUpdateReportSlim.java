package gr.uom.strategicplanning.controllers.entities;

public class IndicatorUpdateReportSlim {
    private String name;
    private Double value;

    public IndicatorUpdateReportSlim(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
