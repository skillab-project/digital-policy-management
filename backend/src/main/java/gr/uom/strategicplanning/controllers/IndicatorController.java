package gr.uom.strategicplanning.controllers;

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

}
