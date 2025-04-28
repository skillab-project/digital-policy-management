package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.Metric;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.MetricRepository;
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
public class MetricServiceTest {

    @Mock
    private MetricRepository metricRepository;

    @Mock
    private IndicatorRepository indicatorRepository;

    @InjectMocks
    private MetricService metricService;

    @Test
    void testGetAllMetrics() {
        List<Metric> metrics = List.of(new Metric("metric1", "a + b"));
        when(metricRepository.findAll()).thenReturn(metrics);

        List<Metric> result = metricService.getAllMetrics();

        assertEquals(1, result.size());
        assertEquals("metric1", result.get(0).getName());
    }

    @Test
    void testGetMetricWithName_Found() {
        Metric metric = new Metric("metric1", "a + b");
        when(metricRepository.findByName("metric1")).thenReturn(Optional.of(metric));

        Metric result = metricService.getMetricWithName("metric1");

        assertEquals("metric1", result.getName());
    }

    @Test
    void testGetMetricWithName_NotFound() {
        when(metricRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            metricService.getMetricWithName("nonexistent");
        });
    }

    @Test
    void testIsNumeric() {
        assertTrue(metricService.isNumeric("123"));
        assertTrue(metricService.isNumeric("-123.45"));
        assertFalse(metricService.isNumeric("abc"));
    }
}