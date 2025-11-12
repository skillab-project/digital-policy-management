package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.KpiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class KpiServiceTest {

    @Mock
    private KpiRepository kpiRepository;

    @Mock
    private IndicatorRepository indicatorRepository;

    @InjectMocks
    private KpiService kpiService;

    @Test
    void testGetAllKpis() {
        List<Kpi> kpis = List.of(new Kpi("kpi1", "a + b"));
        when(kpiRepository.findAll()).thenReturn(kpis);

        List<Kpi> result = kpiService.getAllKpis();

        assertEquals(1, result.size());
        assertEquals("kpi1", result.get(0).getName());
    }

    @Test
    void testGetKpiWithName_Found() {
        Kpi kpi = new Kpi("kpi1", "a + b");
        when(kpiRepository.findByName("kpi1")).thenReturn(Optional.of(kpi));

        Kpi result = kpiService.getKpiWithName("kpi1");

        assertEquals("kpi1", result.getName());
    }

    @Test
    void testGetKpiWithName_NotFound() {
        when(kpiRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            kpiService.getKpiWithName("nonexistent");
        });
    }

    @Test
    void testIsNumeric() {
        assertTrue(kpiService.isNumeric("123"));
        assertTrue(kpiService.isNumeric("-123.45"));
        assertFalse(kpiService.isNumeric("abc"));
    }
}