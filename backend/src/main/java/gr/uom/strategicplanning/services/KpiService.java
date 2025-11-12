package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.KpiCreation;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.repositories.KpiRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class KpiService {

    @Autowired
    KpiRepository kpiRepository;
    @Autowired
    IndicatorRepository indicatorRepository;
    @Autowired
    PolicyRepository policyRepository;

    public List<Kpi> getAllKpis(){
        return kpiRepository.findAll();
    }

    public Kpi getKpiWithName(String name){
        Kpi Kpi = kpiRepository.findByName(name)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KPI with name "+name+" doesn't exist");
                });
        return Kpi;
    }

    @Transactional
    public Kpi createKpi(KpiCreation kpiCreation){
        Policy policy = policyRepository.findByName(kpiCreation.getPolicyName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + kpiCreation.getPolicyName() + " doesn't exist"));
        Optional<Kpi> kpiOptional = kpiRepository.findByName(kpiCreation.getName());
        if(!kpiOptional.isPresent()){
            Kpi kpi = new Kpi(kpiCreation.getName(), kpiCreation.getEquation());
            kpi.setPolicy(policy);
            kpiRepository.save(kpi);
            policy.addKpi(kpi);
            policyRepository.save(policy);
            List<Indicator> indicatorList = new ArrayList<>();
            for(String st: kpiCreation.getEquation().split(" ")){
                if(!isNumeric(st)) {
                    Optional<Indicator> indicatorOptional = indicatorRepository.findBySymbol(st);
                    if(indicatorOptional.isPresent()){
                        indicatorOptional.get().addKpi(kpi);
                        indicatorList.add(indicatorOptional.get());
                    }
                }
            }
            kpi.setIndicatorList(indicatorList);
            return kpi;
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name is used from another kpi");
    }


    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public List<Kpi> getAllKpisOfPolicy(String policyName) {
        Policy policy = policyRepository.findByName(policyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy with name " + policyName + " doesn't exist"));
        return policy.getKpiList();
    }

    public Kpi updateTargetValues(String name, Double targetValue, String targetTime) {
        Kpi kpi = kpiRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kpi with name " + name + " doesn't exist"));
        if(targetValue!=null){
            kpi.setTargetValue(targetValue);
        }
        if(targetTime!=null && !targetTime.isEmpty()){
            kpi.setTargetTime(targetTime);
        }
        kpiRepository.save(kpi);
        return kpi;
    }
}
