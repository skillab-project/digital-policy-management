package gr.uom.strategicplanning.repositories.euproject;

import gr.uom.strategicplanning.models.euproject.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Optional<Project> findByTitle(String title);

    Page<Project> findByTitleContainingIgnoreCase(String contain, Pageable paging);

    Page<Project> findByObjectiveContainingIgnoreCase(String contain, Pageable paging);
}
