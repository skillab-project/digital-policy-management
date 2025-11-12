package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.PolicyCreation;
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

}
