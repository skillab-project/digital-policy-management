package gr.uom.strategicplanning.services;

import gr.uom.strategicplanning.controllers.entities.DeletionResult;
import gr.uom.strategicplanning.controllers.entities.PolicyDeletionSummary;
import gr.uom.strategicplanning.models.Indicator;
import gr.uom.strategicplanning.models.Kpi;
import gr.uom.strategicplanning.models.Policy;
import gr.uom.strategicplanning.repositories.IndicatorReportRepository;
import gr.uom.strategicplanning.repositories.IndicatorRepository;
import gr.uom.strategicplanning.repositories.KpiReportRepository;
import gr.uom.strategicplanning.repositories.KpiRepository;
import gr.uom.strategicplanning.repositories.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for policy / KPI / metric deletion rules.
 *
 * Fixture:
 *   Policy P1 (id 1) -> KPI k1 (uses metrics A, B)
 *   Policy P2 (id 2) -> KPI k2 (uses metric B)
 * So A is only needed by P1, while B is shared with P2.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeletionServicesTest {

    @Mock PolicyRepository policyRepository;
    @Mock KpiRepository kpiRepository;
    @Mock KpiReportRepository kpiReportRepository;
    @Mock IndicatorRepository indicatorRepository;
    @Mock IndicatorReportRepository indicatorReportRepository;

    @InjectMocks PolicyService policyService;
    @InjectMocks KpiService kpiService;
    @InjectMocks IndicatorService indicatorService;

    Policy p1, p2;
    Kpi k1, k2;
    Indicator a, b;

    @BeforeEach
    void setUp() {
        p1 = policy(1L, "P1");
        p2 = policy(2L, "P2");
        a = indicator(10L, "Metric A", "A");
        b = indicator(11L, "Metric B", "B");
        k1 = kpi(100L, "k1", "A + B", p1, a, b);
        k2 = kpi(101L, "k2", "B * 2", p2, b);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(policyRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(kpiRepository.findById(100L)).thenReturn(Optional.of(k1));
        when(kpiRepository.findById(101L)).thenReturn(Optional.of(k2));
        when(indicatorRepository.findById(10L)).thenReturn(Optional.of(a));
        when(indicatorRepository.findById(11L)).thenReturn(Optional.of(b));
        when(kpiRepository.findAllUsingIndicator(10L)).thenAnswer(inv -> users(a));
        when(kpiRepository.findAllUsingIndicator(11L)).thenAnswer(inv -> users(b));
        when(kpiReportRepository.countByKpi_Id(100L)).thenReturn(5L);
        when(kpiReportRepository.countByKpi_Id(101L)).thenReturn(3L);
        when(indicatorReportRepository.countByIndicator_Id(any())).thenReturn(7L);
        when(kpiRepository.save(any(Kpi.class))).thenAnswer(inv -> inv.getArgument(0));
        when(indicatorRepository.save(any(Indicator.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    class PolicyDeletion {
        @Test
        void previewListsKpisAndSplitsSharedMetrics() {
            PolicyDeletionSummary s = policyService.getDeletionPreview(1L);

            assertEquals("P1", s.getPolicyName());
            assertEquals(1, s.getKpis().size());
            assertEquals(5L, s.getKpiReportCount());
            assertEquals(List.of("Metric A"), names(s.getMetricsToDelete()));
            assertEquals(1, s.getMetricsKept().size());
            assertEquals("Metric B", s.getMetricsKept().get(0).getMetric().getName());
            assertEquals("k2", s.getMetricsKept().get(0).getUsedBy().get(0).getName());
            assertEquals("P2", s.getMetricsKept().get(0).getUsedBy().get(0).getPolicyName());
            verify(policyRepository, never()).delete(any());
        }

        @Test
        void deleteRemovesPolicyKpiHistoryAndOnlyOrphanMetrics() {
            // after the policy is deleted, A is no longer used by anything
            when(kpiRepository.findAllUsingIndicator(10L)).thenReturn(List.of(k1)).thenReturn(List.of());

            PolicyDeletionSummary s = policyService.deletePolicy(1L, true);

            verify(kpiReportRepository).deleteAllByKpi_Id(100L);
            verify(policyRepository).delete(p1);
            verify(indicatorReportRepository).deleteAllByIndicator_Id(10L);
            verify(indicatorRepository).delete(a);
            verify(indicatorRepository, never()).delete(b);
            verify(kpiReportRepository, never()).deleteAllByKpi_Id(101L);
            assertTrue(s.getOrphanMetricsDeleted());
            assertFalse(b.getKpiList().contains(k1));
            assertTrue(b.getKpiList().contains(k2));
        }

        @Test
        void deleteCanKeepOrphanMetrics() {
            policyService.deletePolicy(1L, false);

            verify(policyRepository).delete(p1);
            verify(indicatorRepository, never()).delete(any());
        }

        @Test
        void deleteIncludesSoftDeletedKpis() {
            k1.setDeleted(true);
            PolicyDeletionSummary s = policyService.deletePolicy(1L, true);

            assertEquals(1, s.getKpis().size());
            verify(kpiReportRepository).deleteAllByKpi_Id(100L);
        }

        @Test
        void unknownPolicyIs404() {
            when(policyRepository.findById(99L)).thenReturn(Optional.empty());
            ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> policyService.deletePolicy(99L, true));
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }

    @Nested
    class KpiDeletion {
        @Test
        void softDeleteHidesKpiFromPolicyButKeepsHistory() {
            DeletionResult r = kpiService.deleteKpi(100L, false);

            assertEquals(DeletionResult.Mode.SOFT, r.getMode());
            assertTrue(k1.isDeleted());
            assertNotNull(k1.getDeletedAt());
            assertTrue(p1.getKpiList().isEmpty());
            assertEquals(1, p1.getAllKpisIncludingDeleted().size());
            verify(kpiReportRepository, never()).deleteAllByKpi_Id(any());
            verify(kpiRepository, never()).delete(any());
        }

        @Test
        void hardDeleteRemovesKpiAndHistoryButKeepsMetrics() {
            DeletionResult r = kpiService.deleteKpi(100L, true);

            assertEquals(DeletionResult.Mode.HARD, r.getMode());
            assertEquals(5L, r.getRemovedReports());
            verify(kpiReportRepository).deleteAllByKpi_Id(100L);
            verify(kpiRepository).delete(k1);
            verify(indicatorRepository, never()).delete(any());
            assertFalse(p1.getAllKpisIncludingDeleted().contains(k1));
            assertFalse(a.getKpiList().contains(k1));
        }

        @Test
        void restoreBringsBackKpiAndItsSoftDeletedMetrics() {
            k1.setDeleted(true);
            a.setDeleted(true);

            kpiService.restoreKpi(100L);

            assertFalse(k1.isDeleted());
            assertFalse(a.isDeleted());
            assertEquals(1, p1.getKpiList().size());
        }
    }

    @Nested
    class MetricDeletion {
        @Test
        void softDeleteBlockedWhileActiveKpiUsesMetric() {
            ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> indicatorService.deleteIndicator(10L, false));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertTrue(e.getReason().contains("k1 (P1)"));
            assertFalse(a.isDeleted());
        }

        @Test
        void softDeleteAllowedWhenOnlySoftDeletedKpisUseIt() {
            k1.setDeleted(true);
            DeletionResult r = indicatorService.deleteIndicator(10L, false);

            assertEquals(DeletionResult.Mode.SOFT, r.getMode());
            assertTrue(a.isDeleted());
        }

        @Test
        void hardDeleteBlockedWhileAnyKpiUsesMetric() {
            k1.setDeleted(true);
            ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> indicatorService.deleteIndicator(10L, true));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            verify(indicatorRepository, never()).delete(any());
        }

        @Test
        void hardDeleteRemovesUnusedMetricAndHistory() {
            Indicator unused = indicator(12L, "Unused", "U");
            when(indicatorRepository.findById(12L)).thenReturn(Optional.of(unused));
            when(kpiRepository.findAllUsingIndicator(12L)).thenReturn(List.of());

            DeletionResult r = indicatorService.deleteIndicator(12L, true);

            assertEquals(DeletionResult.Mode.HARD, r.getMode());
            assertEquals(7L, r.getRemovedReports());
            verify(indicatorReportRepository).deleteAllByIndicator_Id(12L);
            verify(indicatorRepository).delete(unused);
        }

        @Test
        void restoreMetric() {
            a.setDeleted(true);
            indicatorService.restoreIndicator(10L);
            assertFalse(a.isDeleted());
            assertNull(a.getDeletedAt());
        }
    }

    // ---- helpers -------------------------------------------------------------------------

    private static Policy policy(Long id, String name) {
        Policy p = new Policy(name, "desc", "sector", "EU");
        p.setId(id);
        return p;
    }

    private static Indicator indicator(Long id, String name, String symbol) {
        Indicator i = new Indicator(name, symbol);
        i.setId(id);
        i.setKpiList(new ArrayList<>());
        return i;
    }

    private static Kpi kpi(Long id, String name, String eq, Policy policy, Indicator... indicators) {
        Kpi k = new Kpi(name, eq);
        k.setId(id);
        k.setPolicy(policy);
        policy.addKpi(k);
        k.setIndicatorList(new ArrayList<>(List.of(indicators)));
        for (Indicator i : indicators) {
            i.addKpi(k);
        }
        return k;
    }

    private static List<Kpi> users(Indicator i) {
        return new ArrayList<>(i.getKpiList());
    }

    private static List<String> names(List<gr.uom.strategicplanning.controllers.entities.EntityRef> refs) {
        List<String> out = new ArrayList<>();
        refs.forEach(r -> out.add(r.getName()));
        return out;
    }
}
