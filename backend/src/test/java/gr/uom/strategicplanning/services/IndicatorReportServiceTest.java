package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReport;
import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReportSlim;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.IndicatorReport;
import gr.uom.strategicplanning.repositories.IndicatorReportRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IndicatorReportServiceTest {

    @Mock
    private IndicatorReportRepository indicatorReportRepository;

    @Mock
    private IndicatorRepository indicatorRepository;

    @Mock
    private KpiReportRepository kpiReportRepository;

    @InjectMocks
    private IndicatorReportService indicatorReportService;

    @Test
    void testGetAllIndicatorReports() {
        List<IndicatorReport> reports = List.of(new IndicatorReport(new Date(), new Indicator(), 5.0));
        when(indicatorReportRepository.findAll()).thenReturn(reports);

        List<IndicatorReport> result = indicatorReportService.getAllIndicatorReports();

        assertEquals(1, result.size());
        assertEquals(5.0, result.get(0).getValue());
    }

    @Test
    void testGetAllByDate() {
        Date date = new Date();
        List<IndicatorReport> reports = List.of(new IndicatorReport(date, new Indicator(), 3.0));
        when(indicatorReportRepository.findAllByDate(date)).thenReturn(reports);

        List<IndicatorReport> result = indicatorReportService.getAllByDate(date);

        assertEquals(1, result.size());
        assertEquals(3.0, result.get(0).getValue());
    }

    @Test
    void testCreateIndicatorReport_Success() {
        Date now = new Date();
        Indicator indicator = new Indicator("TestIndicator", "TI");
        indicator.setKpiList(Collections.emptyList()); // no kpi attached

        IndicatorUpdateReport update = new IndicatorUpdateReport();
        update.setName("TestIndicator");
        update.setDate(now);
        update.setValue(7.5);

        when(indicatorRepository.findByName("TestIndicator")).thenReturn(Optional.of(indicator));

        IndicatorReport createdReport = indicatorReportService.createIndicatorReport(update);

        assertNotNull(createdReport);
        assertEquals(7.5, createdReport.getValue());
        verify(indicatorReportRepository, times(1)).save(any(IndicatorReport.class));
    }

    @Test
    void testCreateIndicatorReport_IndicatorNotFound() {
        IndicatorUpdateReport update = new IndicatorUpdateReport();
        update.setName("MissingIndicator");
        update.setDate(new Date());
        update.setValue(5.0);

        when(indicatorRepository.findByName("MissingIndicator")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            indicatorReportService.createIndicatorReport(update);
        });
    }

    @Test
    void testCreateIndicatorReportIncreaseValue_Success() {
        Date previousDate = new Date(System.currentTimeMillis() - 10000);
        Indicator indicator = new Indicator("TestIndicator", "TI");
        indicator.setKpiList(Collections.emptyList()); // no kpi for simplicity

        IndicatorReport previousReport = new IndicatorReport(previousDate, indicator, 5.0);

        IndicatorUpdateReportSlim update = new IndicatorUpdateReportSlim("TestIndicator",3.0);

        when(indicatorRepository.findByName("TestIndicator")).thenReturn(Optional.of(indicator));
        when(indicatorReportRepository.findFirstByIndicator_NameOrderByDateDesc("TestIndicator")).thenReturn(previousReport);

        IndicatorReport createdReport = indicatorReportService.createIndicatorReportIncreaseValue(update);

        assertNotNull(createdReport);
        assertEquals(8.0, createdReport.getValue()); // 5.0 + 3.0
        verify(indicatorReportRepository, times(1)).save(any(IndicatorReport.class));
    }

}