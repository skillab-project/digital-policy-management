package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.MetricReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MetricReportRepository extends JpaRepository<MetricReport, Long> {
    List<MetricReport> findAllByDate(Date date);
    List<MetricReport> findAllByDateBetween(Date dateStart, Date dateEnd);
    List<MetricReport> findAllByMetricName(String name);
    MetricReport findTopByMetricName(String name);
}
