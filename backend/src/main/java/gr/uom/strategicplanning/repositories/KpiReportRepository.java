package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.KpiReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KpiReportRepository extends JpaRepository<KpiReport, Long> {
    List<KpiReport> findAllByDate(Date date);
    List<KpiReport> findAllByDateBetween(Date dateStart, Date dateEnd);
    List<KpiReport> findAllByKpiName(String name);
    KpiReport findTopByKpiName(String name);
}
