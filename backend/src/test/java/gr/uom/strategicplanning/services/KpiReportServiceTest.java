package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.models.KpiReport;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KpiReportServiceTest {

    @Mock
    private KpiReportRepository kpiReportRepository;

    @InjectMocks
    private KpiReportService kpiReportService;

    @Test
    void testGetAllKpiReports() {
        List<KpiReport> reports = List.of(new KpiReport(new Date(), new Kpi(), 10.0));
        when(kpiReportRepository.findAll()).thenReturn(reports);

        List<KpiReport> result = kpiReportService.getAllKpiReports();

        assertEquals(1, result.size());
        assertEquals(10.0, result.get(0).getValue());
    }

    @Test
    void testGetAllKpiReportsByName() {
        List<KpiReport> reports = List.of(new KpiReport(new Date(), new Kpi(), 20.0));
        when(kpiReportRepository.findAllByKpiName("TestKpi")).thenReturn(reports);

        List<KpiReport> result = kpiReportService.getAllKpiReportsByName("TestKpi");

        assertEquals(1, result.size());
        assertEquals(20.0, result.get(0).getValue());
    }
}