package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.MetricReport;
import gr.uom.strategicplanning.repositories.MetricReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricReportService {

    @Autowired
    MetricReportRepository MetricReportRepository;

    public List<MetricReport> getAllMetricReports(){
        return MetricReportRepository.findAll();
    }

    public List<MetricReport> getAllMetricReportsByName(String name){
        return MetricReportRepository.findAllByMetricName(name);
    }
}
