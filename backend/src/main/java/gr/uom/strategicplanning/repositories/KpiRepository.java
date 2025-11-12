package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KpiRepository extends JpaRepository<Kpi,Long> {
    Optional<Kpi> findByName(String name);
}
