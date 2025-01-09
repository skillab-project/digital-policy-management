package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.IndicatorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndicatorReportRepository extends JpaRepository<IndicatorReport, Long> {
    List<IndicatorReport> findAllByDate(Date date);
    List<IndicatorReport> findAllByDateBetween(Date dateStart, Date dateEnd);
    List<IndicatorReport> findAllByIndicatorName(String name);
    List<IndicatorReport> findAllByIndicatorSymbol(String metricSymbol);
    IndicatorReport findTopByIndicatorSymbol(String metricSymbol);
    //Optional<IndicatorReport> findByDateAndIndicatorName(Date date, String name);
}
