package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.DeletionResult;
import gr.uom.strategicplanning.controllers.entities.KpiCreation;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import gr.uom.strategicplanning.repositories.KpiRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class KpiService {

    @Autowired
    KpiRepository kpiRepository;
    @Autowired
    IndicatorRepository indicatorRepository;
    @Autowired
    PolicyRepository policyRepository;
    @Autowired
    KpiReportRepository kpiReportRepository;

    /** All active KPIs (soft-deleted ones are excluded). */
    public List<Kpi> getAllKpis(){
        return kpiRepository.findAllActive();
    }

    /** KPIs that are soft-deleted (in the trash) and can be restored. */
    public List<Kpi> getDeletedKpis(){
        return kpiRepository.findAllDeleted();
    }

    public Kpi getKpiWithName(String name){
        Kpi Kpi = kpiRepository.findByName(name)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KPI with name "+name+" doesn't exist");
                });
        return Kpi;
    }

    public Kpi getKpiWithId(Long id){
        return kpiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "KPI with id " + id + " doesn't exist"));
    }

    @Transactional
    public Kpi createKpi(KpiCreation kpiCreation){
        Policy policy = policyRepository.findByName(kpiCreation.getPolicyName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + kpiCreation.getPolicyName() + " doesn't exist"));
        Optional<Kpi> kpiOptional = kpiRepository.findByName(kpiCreation.getName());
        if(!kpiOptional.isPresent()){
            // Don't allow new KPIs to depend on metrics that are in the trash
            for(String st: kpiCreation.getEquation().split(" ")){
                if(!isNumeric(st)) {
                    Optional<Indicator> indicatorOptional = indicatorRepository.findBySymbol(st);
                    if(indicatorOptional.isPresent() && indicatorOptional.get().isDeleted()){
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Metric " + indicatorOptional.get().getName()
                                + " (" + st + ") is deleted. Restore it before using it in a KPI.");
                    }
                }
            }
            Kpi kpi = new Kpi(kpiCreation.getName(), kpiCreation.getEquation());
            kpi.setPolicy(policy);
            kpiRepository.save(kpi);
            policy.addKpi(kpi);
            policyRepository.save(policy);
            List<Indicator> indicatorList = new ArrayList<>();
            for(String st: kpiCreation.getEquation().split(" ")){
                if(!isNumeric(st)) {
                    Optional<Indicator> indicatorOptional = indicatorRepository.findBySymbol(st);
                    if(indicatorOptional.isPresent()){
                        indicatorOptional.get().addKpi(kpi);
                        indicatorList.add(indicatorOptional.get());
                    }
                }
            }
            kpi.setIndicatorList(indicatorList);
            return kpi;
        }
        if (kpiOptional.get().isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name is used from a deleted kpi. Restore it or delete it permanently first");
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name is used from another kpi");
    }


    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public List<Kpi> getAllKpisOfPolicy(String policyName) {
        Policy policy = policyRepository.findByName(policyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + policyName + " doesn't exist"));
        return policy.getKpiList();
    }

    public Kpi updateTargetValues(String name, Double targetValue, String targetTime) {
        Kpi kpi = kpiRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kpi with name " + name + " doesn't exist"));
        if(targetValue!=null){
            kpi.setTargetValue(targetValue);
        }
        if(targetTime!=null && !targetTime.isEmpty()){
            kpi.setTargetTime(targetTime);
        }
        kpiRepository.save(kpi);
        return kpi;
    }

    /**
     * Deletes a KPI.
     * Soft delete: the KPI is hidden from lists and from its policy, stops being recalculated,
     * but keeps its history and can be restored.
     * Hard delete: the KPI and all its history are removed permanently. Its metrics are kept.
     */
    @Transactional
    public DeletionResult deleteKpi(Long id, boolean hard) {
        Kpi kpi = getKpiWithId(id);
        if (!hard) {
            if (!kpi.isDeleted()) {
                kpi.setDeleted(true);
                kpi.setDeletedAt(new Date());
                kpiRepository.save(kpi);
            }
            return new DeletionResult(kpi.getId(), kpi.getName(), DeletionResult.Mode.SOFT, 0);
        }

        long removedReports = kpiReportRepository.countByKpi_Id(kpi.getId());
        kpiReportRepository.deleteAllByKpi_Id(kpi.getId());
        if (kpi.getIndicatorList() != null) {
            for (Indicator indicator : kpi.getIndicatorList()) {
                indicator.removeKpi(kpi);
            }
            kpi.getIndicatorList().clear();
        }
        Policy policy = kpi.getPolicy();
        if (policy != null && policy.getAllKpisIncludingDeleted() != null) {
            policy.getAllKpisIncludingDeleted().remove(kpi);
        }
        kpiRepository.delete(kpi);
        return new DeletionResult(kpi.getId(), kpi.getName(), DeletionResult.Mode.HARD, removedReports);
    }

    /** Restores a soft-deleted KPI, and any soft-deleted metric it depends on. */
    @Transactional
    public Kpi restoreKpi(Long id) {
        Kpi kpi = getKpiWithId(id);
        if (kpi.getIndicatorList() != null) {
            for (Indicator indicator : kpi.getIndicatorList()) {
                if (indicator.isDeleted()) {
                    indicator.setDeleted(false);
                    indicator.setDeletedAt(null);
                    indicatorRepository.save(indicator);
                }
            }
        }
        kpi.setDeleted(false);
        kpi.setDeletedAt(null);
        return kpiRepository.save(kpi);
    }
}
