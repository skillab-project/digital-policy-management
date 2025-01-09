package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReport;
import gr.uom.strategicplanning.models.MetricReport;
import gr.uom.strategicplanning.models.IndicatorReport;
import gr.uom.strategicplanning.services.MetricReportService;
import gr.uom.strategicplanning.services.IndicatorReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    MetricReportService metricReportService;

    @Autowired
    IndicatorReportService indicatorReportService;



    @PostMapping("/indicator")
    IndicatorReport createIndicatorReport(@RequestBody IndicatorUpdateReport indicatorUpdateReport){
        return indicatorReportService.createIndicatorReport(indicatorUpdateReport);
    }

    @GetMapping("/indicator")
    List<IndicatorReport> getAllIndicatorReportsByName(@RequestParam String indicatorName){
        return indicatorReportService.getAllIndicatorReportsByIndicatorName(indicatorName);
    }

    @GetMapping("/indicator/all")
    List<IndicatorReport> getAllIndicatorReports(){
        return indicatorReportService.getAllIndicatorReports();
    }


    @GetMapping("/metric")
    List<MetricReport> getAllMetricReportsByName(@RequestParam String metricName){
        return metricReportService.getAllMetricReportsByName(metricName);
    }

    @GetMapping("/metric/all")
    List<MetricReport> getAllMetricReports(){
        return metricReportService.getAllMetricReports();
    }

}
