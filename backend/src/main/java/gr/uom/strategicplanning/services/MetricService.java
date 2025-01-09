package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.MetricCreation;
import gr.uom.strategicplanning.models.Metric;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.repositories.MetricRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
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
        Optional<Metric> metricOptional = MetricRepository.findByName(metricCreation.getName());
        if(!metricOptional.isPresent()){
            Metric metric = new Metric(metricCreation.getName(), metricCreation.getEquation());
            MetricRepository.save(metric);
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
}
