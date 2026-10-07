package gr.uom.strategicplanning.repositories;

import gr.uom.strategicplanning.models.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KpiRepository extends JpaRepository<Kpi,Long> {
    Optional<Kpi> findByName(String name);

    @Query("select e from Kpi e where e.deleted is null or e.deleted = false")
    List<Kpi> findAllActive();

    @Query("select e from Kpi e where e.deleted = true")
    List<Kpi> findAllDeleted();

    @Query("select distinct k from Kpi k join k.indicatorList i where i.id = :indicatorId")
    List<Kpi> findAllUsingIndicator(@org.springframework.data.repository.query.Param("indicatorId") Long indicatorId);
}
