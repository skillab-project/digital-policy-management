package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndicatorRepository extends JpaRepository<Indicator,Long> {
    Optional<Indicator> findByName(String metricName);
    Optional<Indicator> findBySymbol(String metricSymbol);
}
