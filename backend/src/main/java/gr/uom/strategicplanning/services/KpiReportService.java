package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.KpiReport;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiReportService {

    @Autowired
    KpiReportRepository kpiReportRepository;

    public List<KpiReport> getAllKpiReports(){
        return kpiReportRepository.findAll();
    }

    public List<KpiReport> getAllKpiReportsByName(String name){
        return kpiReportRepository.findAllByKpiName(name);
    }
}
