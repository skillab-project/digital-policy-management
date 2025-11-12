package gr.uom.strategicplanning.controllers.entities;


public class KpiCreation {
    private String name;
    private String equation;
    private String policyName;

    public KpiCreation(String name, String equation, String policyName) {
        this.name = name;
        this.equation = equation;
        this.policyName = policyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
}
