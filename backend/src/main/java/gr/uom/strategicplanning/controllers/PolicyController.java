package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.PolicyCreation;
import gr.uom.strategicplanning.controllers.entities.PolicyDeletionSummary;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policy")
public class PolicyController {

    @Autowired
    PolicyService policyService;

    @GetMapping("/all")
    List<Policy> getAllPolicies(){
        return policyService.getAllPolicies();
    }

    @GetMapping
    Policy getPolicy(@RequestParam String name){
        return policyService.getPolicyWithName(name);
    }

    @PostMapping
    Policy createPolicy(@RequestBody PolicyCreation policyCreation){
        return policyService.createPolicy(policyCreation);
    }

    /** What deleting this policy would remove (KPIs, KPI history, metrics only this policy uses). Deletes nothing. */
    @GetMapping("/{id}/deletion-preview")
    PolicyDeletionSummary getDeletionPreview(@PathVariable Long id){
        return policyService.getDeletionPreview(id);
    }

    /**
     * Permanently deletes the policy, its KPIs and their history.
     * With deleteOrphanMetrics=true (default) it also deletes the metrics that no other policy uses.
     */
    @DeleteMapping("/{id}")
    PolicyDeletionSummary deletePolicy(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean deleteOrphanMetrics){
        return policyService.deletePolicy(id, deleteOrphanMetrics);
    }
}
