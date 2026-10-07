package gr.uom.strategicplanning.controllers.entities;

import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Kpi;

/**
 * Lightweight reference to a KPI or metric (indicator), used in deletion previews/results
 * and in "usage" responses so the UI can tell the user what is affected.
 */
public class EntityRef {
    private Long id;
    private String name;
    private String symbol;      // metrics only
    private String policyName;  // KPIs only
    private boolean deleted;    // true if the entity is currently soft-deleted (in the trash)

    public EntityRef() {
    }

    public static EntityRef of(Kpi kpi) {
        EntityRef ref = new EntityRef();
        ref.id = kpi.getId();
        ref.name = kpi.getName();
        ref.policyName = kpi.getPolicyName();
        ref.deleted = kpi.isDeleted();
        return ref;
    }

    public static EntityRef of(Indicator indicator) {
        EntityRef ref = new EntityRef();
        ref.id = indicator.getId();
        ref.name = indicator.getName();
        ref.symbol = indicator.getSymbol();
        ref.deleted = indicator.isDeleted();
        return ref;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getPolicyName() {
        return policyName;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
