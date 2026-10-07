package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IndicatorRepository extends JpaRepository<Indicator,Long> {
    Optional<Indicator> findByName(String name);
    Optional<Indicator> findBySymbol(String symbol);

    @Query("select e from Indicator e where e.deleted is null or e.deleted = false")
    List<Indicator> findAllActive();

    @Query("select e from Indicator e where e.deleted = true")
    List<Indicator> findAllDeleted();
}
