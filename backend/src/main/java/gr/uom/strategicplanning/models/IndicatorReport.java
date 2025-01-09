package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public class IndicatorReport {
    @Id
    @GeneratedValue
    private Long id;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date date;
    @ManyToOne
    private Indicator indicator;
    private Double value;

    public IndicatorReport() {
    }

    public IndicatorReport(Date date, Indicator indicator, Double value) {
        this.date = date;
        this.indicator = indicator;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
