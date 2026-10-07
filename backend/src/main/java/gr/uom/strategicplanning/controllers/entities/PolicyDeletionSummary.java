package gr.uom.strategicplanning.controllers.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes what deleting a policy removes. Returned both by the preview endpoint
 * (nothing deleted yet) and by the delete endpoint (what was actually deleted).
 */
public class PolicyDeletionSummary {
    private Long policyId;
    private String policyName;
    /** All KPIs of the policy (active and soft-deleted) - always deleted together with the policy. */
    private List<EntityRef> kpis = new ArrayList<>();
    /** Number of KPI history values that are removed with those KPIs. */
    private long kpiReportCount;
    /** Metrics used only by this policy's KPIs - deleted (with their history) when deleteOrphanMetrics=true. */
    private List<EntityRef> metricsToDelete = new ArrayList<>();
    /** Metrics also used by KPIs of other policies - always kept. */
    private List<MetricUsage> metricsKept = new ArrayList<>();
    /** Only set in the delete response: whether the orphan metrics were actually removed. */
    private Boolean orphanMetricsDeleted;

    public static class MetricUsage {
        private EntityRef metric;
        private List<EntityRef> usedBy = new ArrayList<>();

        public MetricUsage() {
        }

        public MetricUsage(EntityRef metric, List<EntityRef> usedBy) {
            this.metric = metric;
            this.usedBy = usedBy;
        }

        public EntityRef getMetric() {
            return metric;
        }

        public List<EntityRef> getUsedBy() {
            return usedBy;
        }
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public List<EntityRef> getKpis() {
        return kpis;
    }

    public long getKpiReportCount() {
        return kpiReportCount;
    }

    public void setKpiReportCount(long kpiReportCount) {
        this.kpiReportCount = kpiReportCount;
    }

    public List<EntityRef> getMetricsToDelete() {
        return metricsToDelete;
    }

    public List<MetricUsage> getMetricsKept() {
        return metricsKept;
    }

    public Boolean getOrphanMetricsDeleted() {
        return orphanMetricsDeleted;
    }

    public void setOrphanMetricsDeleted(Boolean orphanMetricsDeleted) {
        this.orphanMetricsDeleted = orphanMetricsDeleted;
    }
}
