package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReport;
import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReportSlim;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.models.KpiReport;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.IndicatorReport;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import gr.uom.strategicplanning.repositories.IndicatorReportRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.graalvm.polyglot.*;


import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class IndicatorReportService {

    @Autowired
    IndicatorReportRepository indicatorReportRepository;
    @Autowired
    IndicatorRepository indicatorRepository;
    @Autowired
    KpiReportRepository kpiReportRepository;

    public List<IndicatorReport> getAllByDate(Date date){
        return indicatorReportRepository.findAllByDate(date);
    }

    public List<IndicatorReport> getAllByDateBetween(Date date1, Date date2){
        return indicatorReportRepository.findAllByDateBetween(date1, date2);
    }

    public List<IndicatorReport> getAllIndicatorReports(){
        return indicatorReportRepository.findAll();
    }

    public List<IndicatorReport> getAllIndicatorReportsByIndicatorName(String name){
        return indicatorReportRepository.findAllByIndicatorName(name);
    }

    public IndicatorReport getLastIndicatorReportsByIndicatorName(String name){
        return indicatorReportRepository.findFirstByIndicator_NameOrderByDateDesc(name);
    }

    public IndicatorReport createIndicatorReport(IndicatorUpdateReport indicatorUpdateReport){
        Indicator indicator = indicatorRepository.findByName(indicatorUpdateReport.getName())
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with name "+indicatorUpdateReport.getName()+" doesn't exist");
                });
        IndicatorReport indicatorReport = new IndicatorReport(indicatorUpdateReport.getDate(), indicator, indicatorUpdateReport.getValue());
        indicatorReportRepository.save(indicatorReport);

        //update all kpi that have this indicators
        // by creating a KpiReport for each one
        for(Kpi i: indicator.getKpiList()){
            Double value = calculateKpi(i);
            KpiReport kpiReport = new KpiReport(indicatorUpdateReport.getDate(), i, value);
            kpiReportRepository.save(kpiReport);
        }

        return indicatorReport;
    }

    public IndicatorReport createIndicatorReportIncreaseValue(IndicatorUpdateReportSlim indicatorUpdateReportSlim){
        Indicator indicator = indicatorRepository.findByName(indicatorUpdateReportSlim.getName())
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with name "+indicatorUpdateReportSlim.getName()+" doesn't exist");
                });
        Double newValue = getLastIndicatorReportsByIndicatorName(indicator.getName()).getValue() +indicatorUpdateReportSlim.getValue();
        Date currentDate = new Date();
        IndicatorReport indicatorReport = new IndicatorReport(currentDate, indicator, newValue);
        indicatorReportRepository.save(indicatorReport);

        //update all kpi that have this indicators
        // by creating a KpiReport for each one
        for(Kpi i: indicator.getKpiList()){
            Double value = calculateKpi(i);
            KpiReport kpiReport = new KpiReport(currentDate, i, value);
            kpiReportRepository.save(kpiReport);
        }

        return indicatorReport;
    }

    private Double calculateKpi(Kpi i) {
        //change kpis with their last value
        String[] equation = i.getEquation().split(" ");
        String equationToSolve = "";
        List<Indicator> indicatorList = indicatorRepository.findAll();
        for (String str:equation){
            boolean isIndicator = false;
            for(Indicator indicator : indicatorList){
                if(indicator.getSymbol().equals(str)){
                    //get last value of this kpi
                    List<IndicatorReport> indicatorReportList = indicatorReportRepository.findAllByIndicatorSymbol(indicator.getSymbol());
                    if(!indicatorReportList.isEmpty()) {
                        IndicatorReport indicatorReport =  Collections.max(indicatorReportList, Comparator.comparing(IndicatorReport::getDate));
                        equationToSolve += indicatorReport.getValue();
                        isIndicator = true;
                    }
                }
            }
            if(!isIndicator){
                equationToSolve += str;
            }
        }


        // Create a context for evaluating JavaScript code
        try (Context context = Context.create()) {
            // Evaluate the equation string in JavaScript and return the result
            Value result = context.eval("js", equationToSolve);

            // Print the result
            System.out.println("Result: " + result.asDouble());
            return result.asDouble();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Transactional
    public void deleteIndicatorReport(IndicatorUpdateReport indicatorUpdateReport) {
        /*Indicator indicator = indicatorRepository.findByName(indicatorUpdateReport.getName())
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with name "+indicatorUpdateReport.getName()+" doesn't exist");
                });
        IndicatorReport indicatorReport = indicatorReportRepository.findByDateAndIndicatorName(indicatorUpdateReport.getDate(),indicatorUpdateReport.getName())
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report with name  "+indicatorUpdateReport.getName()+" and date "+ indicatorUpdateReport.getDate() +" doesn't exist");
                });


        //delete Kpi Reports of this indicator report
        List<KpiReport> kpiReport = kpiReportRepository.findAllByDate(indicatorReport.getDate());
        for (Kpi kpi: indicator.getKpiList()){
            for (KpiReport mr: kpiReport) {
                if (kpi.getName().equals(mr.getKpi().getName())){
                    KpiReportRepository.delete(mr);
                }
            }
        }

        //toDo
        //Update values of future Kpi Reports which used this indicator value

        indicatorReportRepository.delete(indicatorReport);
        */
    }
}
