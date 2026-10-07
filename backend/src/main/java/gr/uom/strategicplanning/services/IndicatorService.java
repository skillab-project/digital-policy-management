package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.DeletionResult;
import gr.uom.strategicplanning.controllers.entities.EntityRef;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.repositories.IndicatorReportRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.KpiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IndicatorService {

    @Autowired
    IndicatorRepository indicatorRepository;
    @Autowired
    IndicatorReportRepository indicatorReportRepository;
    @Autowired
    KpiRepository kpiRepository;

    /** All active metrics (soft-deleted ones are excluded). */
    public List<Indicator> getAllIndicators(){
        return indicatorRepository.findAllActive();
    }

    /** Metrics that are soft-deleted (in the trash) and can be restored. */
    public List<Indicator> getDeletedIndicators(){
        return indicatorRepository.findAllDeleted();
    }

    public Indicator getIndicatorWithName(String name){
        Indicator indicator = indicatorRepository.findByName(name)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with name "+name+" doesn't exist");
                });
        return indicator;
    }

    public Indicator getIndicatorWithSymbol(String symbol){
        Indicator indicator = indicatorRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with symbol "+symbol+" doesn't exist");
                });
        return indicator;
    }

    public Indicator getIndicatorWithId(Long id){
        return indicatorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with id " + id + " doesn't exist"));
    }

    public Indicator createIndicator(Indicator indicator){
        Optional<Indicator> indicatorOptional1 = indicatorRepository.findByName(indicator.getName());
        Optional<Indicator> indicatorOptional2 = indicatorRepository.findBySymbol(indicator.getSymbol());
        if(!indicatorOptional1.isPresent() && !indicatorOptional2.isPresent()){
            indicator.setKpiList(new ArrayList<>());
            indicator.setDeleted(false);
            indicatorRepository.save(indicator);
            return indicator;
        }
        boolean takenByDeleted = indicatorOptional1.map(Indicator::isDeleted).orElse(false)
                || indicatorOptional2.map(Indicator::isDeleted).orElse(false);
        if (takenByDeleted) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name or Symbol is used from a deleted Indicator. Restore it or delete it permanently first");
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name or Symbol is used from another Indicator");
    }

    /** KPIs (active and soft-deleted) whose equation uses this metric. */
    @Transactional
    public List<EntityRef> getIndicatorUsage(Long id){
        getIndicatorWithId(id);
        return kpiRepository.findAllUsingIndicator(id).stream().map(EntityRef::of).collect(Collectors.toList());
    }

    /**
     * Deletes a metric.
     * Soft delete: hidden from lists, no new values can be added, history is kept and it can be restored.
     *   Not allowed while an active KPI uses it.
     * Hard delete: the metric and all its history are removed permanently.
     *   Not allowed while any KPI (active or soft-deleted) uses it.
     */
    @Transactional
    public DeletionResult deleteIndicator(Long id, boolean hard){
        Indicator indicator = getIndicatorWithId(id);
        List<Kpi> users = kpiRepository.findAllUsingIndicator(id);

        if (!hard) {
            List<Kpi> activeUsers = users.stream().filter(k -> !k.isDeleted()).collect(Collectors.toList());
            if (!activeUsers.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Metric " + indicator.getName()
                        + " is used by active KPIs: " + describe(activeUsers) + ". Delete those KPIs first.");
            }
            if (!indicator.isDeleted()) {
                indicator.setDeleted(true);
                indicator.setDeletedAt(new Date());
                indicatorRepository.save(indicator);
            }
            return new DeletionResult(indicator.getId(), indicator.getName(), DeletionResult.Mode.SOFT, 0);
        }

        if (!users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Metric " + indicator.getName()
                    + " is used by KPIs: " + describe(users) + ". Permanently delete those KPIs first.");
        }
        long removedReports = indicatorReportRepository.countByIndicator_Id(id);
        indicatorReportRepository.deleteAllByIndicator_Id(id);
        indicatorRepository.delete(indicator);
        return new DeletionResult(indicator.getId(), indicator.getName(), DeletionResult.Mode.HARD, removedReports);
    }

    @Transactional
    public Indicator restoreIndicator(Long id){
        Indicator indicator = getIndicatorWithId(id);
        indicator.setDeleted(false);
        indicator.setDeletedAt(null);
        return indicatorRepository.save(indicator);
    }

    private String describe(List<Kpi> kpis) {
        return kpis.stream()
                .map(k -> k.getName() + (k.getPolicyName() != null ? " (" + k.getPolicyName() + ")" : "") + (k.isDeleted() ? " [deleted]" : ""))
                .collect(Collectors.joining(", "));
    }
}
