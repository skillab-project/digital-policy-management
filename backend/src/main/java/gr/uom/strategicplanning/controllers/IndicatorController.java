package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.DeletionResult;
import gr.uom.strategicplanning.controllers.entities.EntityRef;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.services.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indicator")
public class IndicatorController {

    @Autowired
    IndicatorService indicatorService;

    @GetMapping("/all")
    List<Indicator> getAllIndicators(){
        return indicatorService.getAllIndicators();
    }

    @GetMapping
    Indicator getIndicator(@RequestParam(required = false) String name, @RequestParam(required = false) String symbol){
        if(name!=null){
            return indicatorService.getIndicatorWithName(name);
        }
        if(symbol!=null){
            return indicatorService.getIndicatorWithSymbol(symbol);
        }
        return null;
    }

    @PostMapping
    Indicator createIndicator(@RequestBody Indicator indicator){
        System.out.println(indicator.getName());
        System.out.println(indicator.getSymbol());
        return indicatorService.createIndicator(indicator);
    }

    /** Soft-deleted metrics (the trash). */
    @GetMapping("/deleted")
    List<Indicator> getDeletedIndicators(){
        return indicatorService.getDeletedIndicators();
    }

    /** KPIs (active and soft-deleted) that use this metric in their equation. */
    @GetMapping("/{id}/usage")
    List<EntityRef> getIndicatorUsage(@PathVariable Long id){
        return indicatorService.getIndicatorUsage(id);
    }

    /**
     * Soft delete by default (restorable, blocked while active KPIs use it);
     * hard=true removes the metric and its history permanently (blocked while any KPI uses it).
     */
    @DeleteMapping("/{id}")
    DeletionResult deleteIndicator(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean hard){
        return indicatorService.deleteIndicator(id, hard);
    }

    @PostMapping("/{id}/restore")
    Indicator restoreIndicator(@PathVariable Long id){
        return indicatorService.restoreIndicator(id);
    }
}
