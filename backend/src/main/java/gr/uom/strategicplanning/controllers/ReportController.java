package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReport;
import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReportSlim;
import gr.uom.strategicplanning.models.KpiReport;
import gr.uom.strategicplanning.models.IndicatorReport;
import gr.uom.strategicplanning.services.KpiReportService;
import gr.uom.strategicplanning.services.IndicatorReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    KpiReportService kpiReportService;

    @Autowired
    IndicatorReportService indicatorReportService;



    @PostMapping("/indicator")
    IndicatorReport createIndicatorReport(@RequestBody IndicatorUpdateReport indicatorUpdateReport){
        return indicatorReportService.createIndicatorReport(indicatorUpdateReport);
    }

    @PostMapping("/indicator/increaseby")
    IndicatorReport createIndicatorReportIncreaseValue(@RequestBody IndicatorUpdateReportSlim indicatorUpdateReportSlim){
        return indicatorReportService.createIndicatorReportIncreaseValue(indicatorUpdateReportSlim);
    }

    @GetMapping("/indicator")
    List<IndicatorReport> getAllIndicatorReportsByName(@RequestParam String indicatorName){
        return indicatorReportService.getAllIndicatorReportsByIndicatorName(indicatorName);
    }

    @GetMapping("/indicator/all")
    List<IndicatorReport> getAllIndicatorReports(){
        return indicatorReportService.getAllIndicatorReports();
    }


    @GetMapping("/kpi")
    List<KpiReport> getAllKpiReportsByName(@RequestParam String kpiName){
        return kpiReportService.getAllKpiReportsByName(kpiName);
    }

    @GetMapping("/kpi/all")
    List<KpiReport> getAllKpiReports(){
        return kpiReportService.getAllKpiReports();
    }

}
