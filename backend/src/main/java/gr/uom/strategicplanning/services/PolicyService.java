package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.PolicyCreation;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.repositories.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {
    @Autowired
    PolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyWithName(String name) {
        return policyRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + name + " doesn't exist"));
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
}
