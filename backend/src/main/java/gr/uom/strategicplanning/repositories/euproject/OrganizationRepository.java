package gr.uom.strategicplanning.repositories.euproject;

import gr.uom.strategicplanning.models.euproject.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {
    Optional<Organization> findByAcronym(String acronym);
    Optional<Organization> findByName(String name);
    Optional<Organization> findByNameIgnoreCase(String name);
}
