package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.MetricCreation;
import gr.uom.strategicplanning.models.Metric;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.repositories.MetricRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class MetricService {

    @Autowired
    MetricRepository MetricRepository;
    @Autowired
    IndicatorRepository indicatorRepository;
    @Autowired
    PolicyRepository policyRepository;

    public List<Metric> getAllMetrics(){
        return MetricRepository.findAll();
    }

    public Metric getMetricWithName(String name){
        Metric Metric = MetricRepository.findByName(name)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metric with name "+name+" doesn't exist");
                });
        return Metric;
    }

    @Transactional
    public Metric createMetric(MetricCreation metricCreation){
        Policy policy = policyRepository.findByName(metricCreation.getPolicyName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + metricCreation.getPolicyName() + " doesn't exist"));
        Optional<Metric> metricOptional = MetricRepository.findByName(metricCreation.getName());
        if(!metricOptional.isPresent()){
            Metric metric = new Metric(metricCreation.getName(), metricCreation.getEquation());
            metric.setPolicy(policy);
            MetricRepository.save(metric);
            policy.addMetric(metric);
            policyRepository.save(policy);
            List<Indicator> indicatorList = new ArrayList<>();
            for(String st: metricCreation.getEquation().split(" ")){
                if(!isNumeric(st)) {
                    Optional<Indicator> indicatorOptional = indicatorRepository.findBySymbol(st);
                    if(indicatorOptional.isPresent()){
                        indicatorOptional.get().addMetric(metric);
                        indicatorList.add(indicatorOptional.get());
                    }
                }
            }
            metric.setIndicatorList(indicatorList);
            return metric;
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name is used from another metric");
    }


    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public List<Metric> getAllMetricsOfPolicy(String policyName) {
        Policy policy = policyRepository.findByName(policyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + policyName + " doesn't exist"));
        return policy.getMetricList();
    }

    public Metric updateTargetValues(String name, Double targetValue, String targetTime) {
        Metric metric = MetricRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Metric with name " + name + " doesn't exist"));
        if(targetValue!=null){
            metric.setTargetValue(targetValue);
        }
        if(targetTime!=null && !targetTime.isEmpty()){
            metric.setTargetTime(targetTime);
        }
        MetricRepository.save(metric);
        return metric;
    }
}
