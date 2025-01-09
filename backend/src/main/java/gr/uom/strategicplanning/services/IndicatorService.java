package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IndicatorService {

    @Autowired
    IndicatorRepository indicatorRepository;

    public List<Indicator> getAllIndicators(){
        return indicatorRepository.findAll();
    }

    public Indicator getIndicatorWithName(String name){
        Indicator indicator = indicatorRepository.findByName(name)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with name "+name+" doesn't exist");
                });
        return indicator;
    }

    public Indicator getIndicatorWithSymbol(String symbol){
        Indicator indicator = indicatorRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicator with symbol "+symbol+" doesn't exist");
                });
        return indicator;
    }

    public Indicator createIndicator(Indicator indicator){
        Optional<Indicator> indicatorOptional1 = indicatorRepository.findByName(indicator.getName());
        Optional<Indicator> indicatorOptional2 = indicatorRepository.findBySymbol(indicator.getSymbol());
        if(!indicatorOptional1.isPresent() && !indicatorOptional2.isPresent()){
            indicator.setMetricList(new ArrayList<>());
            indicatorRepository.save(indicator);
            return indicator;
        }
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Name or Symbol is used from another Indicator");
    }
}
