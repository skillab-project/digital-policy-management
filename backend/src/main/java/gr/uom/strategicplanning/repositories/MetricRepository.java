package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetricRepository extends JpaRepository<Metric,Long> {
    Optional<Metric> findByName(String name);
}
