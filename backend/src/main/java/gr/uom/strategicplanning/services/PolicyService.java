package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.EntityRef;
import gr.uom.strategicplanning.controllers.entities.PolicyCreation;
import gr.uom.strategicplanning.controllers.entities.PolicyDeletionSummary;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.repositories.IndicatorReportRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import gr.uom.strategicplanning.repositories.KpiRepository;
import gr.uom.strategicplanning.repositories.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolicyService {
    @Autowired
    PolicyRepository policyRepository;
    @Autowired
    KpiRepository kpiRepository;
    @Autowired
    KpiReportRepository kpiReportRepository;
    @Autowired
    IndicatorRepository indicatorRepository;
    @Autowired
    IndicatorReportRepository indicatorReportRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyWithName(String name) {
        return policyRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + name + " doesn't exist"));
    }

    public Policy getPolicyWithId(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with id " + id + " doesn't exist"));
    }

    public Policy createPolicy(PolicyCreation policyCreation) {
        Optional<Policy> policyOptional = policyRepository.findByName(policyCreation.getName());
        if(policyOptional.isEmpty()){
            Policy policy = new Policy(policyCreation.getName(),policyCreation.getDescription(),policyCreation.getSector(),policyCreation.getRegion());
            policyRepository.save(policy);
            return policy;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + policyCreation.getName() + " exist");
    }

    /**
     * Computes what deleting the policy would remove, without deleting anything:
     * all its KPIs (active and soft-deleted) with their history, plus the metrics that
     * are used only by this policy's KPIs. Metrics that are also used by KPIs of other
     * policies are listed as kept, together with the KPIs that still need them.
     */
    @Transactional
    public PolicyDeletionSummary getDeletionPreview(Long policyId) {
        return buildSummary(getPolicyWithId(policyId));
    }

    /**
     * Hard-deletes a policy, all its KPIs and their history. When deleteOrphanMetrics is true,
     * metrics that no other policy's KPI uses are deleted too (with their history).
     * Metrics needed by other policies are never touched.
     */
    @Transactional
    public PolicyDeletionSummary deletePolicy(Long policyId, boolean deleteOrphanMetrics) {
        Policy policy = getPolicyWithId(policyId);
        PolicyDeletionSummary summary = buildSummary(policy);

        List<Kpi> kpis = policy.getAllKpisIncludingDeleted() != null
                ? new ArrayList<>(policy.getAllKpisIncludingDeleted())
                : new ArrayList<>();
        for (Kpi kpi : kpis) {
            kpiReportRepository.deleteAllByKpi_Id(kpi.getId());
            if (kpi.getIndicatorList() != null) {
                for (Indicator indicator : kpi.getIndicatorList()) {
                    indicator.removeKpi(kpi);
                }
                kpi.getIndicatorList().clear();
            }
        }
        // KPIs are removed through the policy's cascade (CascadeType.ALL + orphanRemoval)
        policyRepository.delete(policy);
        policyRepository.flush();

        if (deleteOrphanMetrics) {
            for (EntityRef ref : summary.getMetricsToDelete()) {
                indicatorRepository.findById(ref.getId()).ifPresent(indicator -> {
                    // Re-check after the KPIs are gone, in case something changed concurrently
                    if (kpiRepository.findAllUsingIndicator(indicator.getId()).isEmpty()) {
                        indicatorReportRepository.deleteAllByIndicator_Id(indicator.getId());
                        indicatorRepository.delete(indicator);
                    }
                });
            }
        }
        summary.setOrphanMetricsDeleted(deleteOrphanMetrics);
        return summary;
    }

    private PolicyDeletionSummary buildSummary(Policy policy) {
        PolicyDeletionSummary summary = new PolicyDeletionSummary();
        summary.setPolicyId(policy.getId());
        summary.setPolicyName(policy.getName());

        List<Kpi> kpis = policy.getAllKpisIncludingDeleted() != null ? policy.getAllKpisIncludingDeleted() : List.of();
        long reportCount = 0;
        Map<Long, Indicator> candidateMetrics = new LinkedHashMap<>();
        for (Kpi kpi : kpis) {
            summary.getKpis().add(EntityRef.of(kpi));
            reportCount += kpiReportRepository.countByKpi_Id(kpi.getId());
            if (kpi.getIndicatorList() != null) {
                for (Indicator indicator : kpi.getIndicatorList()) {
                    candidateMetrics.putIfAbsent(indicator.getId(), indicator);
                }
            }
        }
        summary.setKpiReportCount(reportCount);

        for (Indicator indicator : candidateMetrics.values()) {
            List<EntityRef> usedElsewhere = kpiRepository.findAllUsingIndicator(indicator.getId()).stream()
                    .filter(k -> k.getPolicy() == null || !Objects.equals(k.getPolicy().getId(), policy.getId()))
                    .map(EntityRef::of)
                    .collect(Collectors.toList());
            if (usedElsewhere.isEmpty()) {
                summary.getMetricsToDelete().add(EntityRef.of(indicator));
            } else {
                summary.getMetricsKept().add(new PolicyDeletionSummary.MetricUsage(EntityRef.of(indicator), usedElsewhere));
            }
        }
        return summary;
    }
}
