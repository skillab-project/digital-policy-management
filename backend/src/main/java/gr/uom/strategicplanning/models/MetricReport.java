package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class MetricReport {

    @Id
    @GeneratedValue
    private Long id;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date date;
    @ManyToOne
    private Metric metric;
    private Double value;

    public MetricReport() {
    }

    public MetricReport(Date date, Metric Metric, Double value) {
        this.date = date;
        this.metric = Metric;
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

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric Metric) {
        this.metric = Metric;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
