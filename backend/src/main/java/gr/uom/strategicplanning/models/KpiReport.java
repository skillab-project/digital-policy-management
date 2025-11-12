package gr.uom.strategicplanning.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class KpiReport {

    @Id
    @GeneratedValue
    private Long id;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date date;
    @ManyToOne
    private Kpi kpi;
    private Double value;

    public KpiReport() {
    }

    public KpiReport(Date date, Kpi Kpi, Double value) {
        this.date = date;
        this.kpi = Kpi;
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

    public Kpi getKpi() {
        return kpi;
    }

    public void setKpi(Kpi Kpi) {
        this.kpi = Kpi;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
