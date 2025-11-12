package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.KpiCreation;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.services.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kpi")
public class KpiController {

    @Autowired
    KpiService kpiService;

    @GetMapping("/all")
    List<Kpi> getAllKpis(){
        return kpiService.getAllKpis();
    }

    @GetMapping("/allPolicy")
    List<Kpi> getAllKpisOfPolicy(@RequestParam String policyName){
        return kpiService.getAllKpisOfPolicy(policyName);
    }

    @GetMapping
    Kpi getKpi(@RequestParam String name){
        return kpiService.getKpiWithName(name);
    }

    @PostMapping
    Kpi createKpi(@RequestBody KpiCreation kpiCreation){
        return kpiService.createKpi(kpiCreation);
    }

    @PutMapping
    Kpi updateTargetValues(@RequestParam String name, @RequestParam(required = false) Double targetValue, @RequestParam(required = false) String targetTime){
        return kpiService.updateTargetValues(name, targetValue, targetTime);
    }
}
