package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.DeletionResult;
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

    /** Soft-deleted KPIs (the trash). */
    @GetMapping("/deleted")
    List<Kpi> getDeletedKpis(){
        return kpiService.getDeletedKpis();
    }

    /** Soft delete by default (restorable); hard=true removes the KPI and its history permanently. */
    @DeleteMapping("/{id}")
    DeletionResult deleteKpi(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean hard){
        return kpiService.deleteKpi(id, hard);
    }

    @PostMapping("/{id}/restore")
    Kpi restoreKpi(@PathVariable Long id){
        return kpiService.restoreKpi(id);
    }
}
