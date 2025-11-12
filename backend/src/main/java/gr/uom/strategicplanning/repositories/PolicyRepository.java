package gr.uom.strategicplanning.repositories;


import gr.uom.strategicplanning.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy,Long> {
    Optional<Policy> findByName(String name);
}
