package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.MetricCreation;
import gr.uom.strategicplanning.models.Metric;
import gr.uom.strategicplanning.services.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metric")
public class MetricController {

    @Autowired
    MetricService MetricService;

    @GetMapping("/all")
    List<Metric> getAllMertics(){
        return MetricService.getAllMetrics();
    }

    @GetMapping
    Metric getMetric(@RequestParam String name){
        return MetricService.getMetricWithName(name);
    }

    @PostMapping
    Metric createMetric(@RequestBody MetricCreation metricCreation){
        return MetricService.createMetric(metricCreation);
    }
}
