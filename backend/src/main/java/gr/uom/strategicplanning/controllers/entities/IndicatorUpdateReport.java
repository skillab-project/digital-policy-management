package gr.uom.strategicplanning.controllers.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class IndicatorUpdateReport {
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date date;
    private String name;
    private Double value;

    public IndicatorUpdateReport(Date date, String name, Double value) {
        this.date = date;
        this.name = name;
        this.value = value;
    }

    public IndicatorUpdateReport() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
