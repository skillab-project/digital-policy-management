package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class IndicatorServiceTest {

    @Mock
    private IndicatorRepository indicatorRepository;

    @InjectMocks
    private IndicatorService indicatorService;

    @Test
    void testGetAllIndicators() {
        List<Indicator> indicators = List.of(new Indicator("ind1", "A"));
        when(indicatorRepository.findAll()).thenReturn(indicators);

        List<Indicator> result = indicatorService.getAllIndicators();

        assertEquals(1, result.size());
        assertEquals("ind1", result.get(0).getName());
    }

    @Test
    void testGetIndicatorWithName_Found() {
        Indicator indicator = new Indicator("ind1", "A");
        when(indicatorRepository.findByName("ind1")).thenReturn(Optional.of(indicator));

        Indicator result = indicatorService.getIndicatorWithName("ind1");

        assertEquals("ind1", result.getName());
    }

    @Test
    void testGetIndicatorWithName_NotFound() {
        when(indicatorRepository.findByName("missing")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            indicatorService.getIndicatorWithName("missing");
        });
    }
}