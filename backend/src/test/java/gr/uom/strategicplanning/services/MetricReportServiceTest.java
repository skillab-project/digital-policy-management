package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.Metric;
import gr.uom.strategicplanning.models.MetricReport;
import gr.uom.strategicplanning.repositories.MetricReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MetricReportServiceTest {

    @Mock
    private MetricReportRepository metricReportRepository;

    @InjectMocks
    private MetricReportService metricReportService;

    @Test
    void testGetAllMetricReports() {
        List<MetricReport> reports = List.of(new MetricReport(new Date(), new Metric(), 10.0));
        when(metricReportRepository.findAll()).thenReturn(reports);

        List<MetricReport> result = metricReportService.getAllMetricReports();

        assertEquals(1, result.size());
        assertEquals(10.0, result.get(0).getValue());
    }

    @Test
    void testGetAllMetricReportsByName() {
        List<MetricReport> reports = List.of(new MetricReport(new Date(), new Metric(), 20.0));
        when(metricReportRepository.findAllByMetricName("TestMetric")).thenReturn(reports);

        List<MetricReport> result = metricReportService.getAllMetricReportsByName("TestMetric");

        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0).getValue());
    }
}