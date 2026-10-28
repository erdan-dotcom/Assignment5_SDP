package strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import strategy.core.RoutePlanner;
import strategy.core.StrategySelector;
import strategy.model.*;
import strategy.strategies.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RoutePlannerTest {
    private List<Route> routes;
    private Location start;
    private Location end;

    @BeforeEach
    void setUp() {
        start = new Location("A", 0, 0);
        end = new Location("B", 1, 1);
        routes = List.of(
                new Route("R1_Fast", start, end, 20.0, 10, 3000, 5.0, 2, true, 80),
                new Route("R2_Cheap", start, end, 25.0, 50, 500, 4.0, 0, true, 70),
                new Route("R3_Eco", start, end, 15.0, 40, 1500, 1.0, 5, true, 90),
                new Route("R4_Safe", start, end, 18.0, 30, 2000, 3.0, 0, true, 100),
                new Route("R5_Closed", start, end, 5.0, 5, 100, 0.5, 0, false, 50)
        );
    }

    @Test
    void testFastestStrategy() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertEquals("R1_Fast", decision.selectedRoute().id());
    }

    @Test
    void testCheapestStrategy() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertEquals("R2_Cheap", decision.selectedRoute().id());
    }

    @Test
    void testEcoFriendlyStrategy() {
        RoutePlanner planner = new RoutePlanner(new EcoFriendlyRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertEquals("R3_Eco", decision.selectedRoute().id());
    }

    @Test
    void testBalancedStrategy() {
        RoutePlanner planner = new RoutePlanner(new BalancedRouteStrategy(0.25, 0.25, 0.25, 0.25));
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertNotNull(decision.selectedRoute());
    }

    @Test
    void testSafestStrategy() {
        RoutePlanner planner = new RoutePlanner(new SafestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertEquals("R4_Safe", decision.selectedRoute().id());
    }

    @Test
    void testRuntimeSwitching() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        assertEquals("R1_Fast", planner.plan(routes, req).selectedRoute().id());
        planner.setStrategy(new CheapestRouteStrategy());
        assertEquals("R2_Cheap", planner.plan(routes, req).selectedRoute().id());
    }

    @Test
    void testAutomaticStrategySelection() {
        StrategySelector selector = new StrategySelector();
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.URGENT, 5000, 60, false);
        assertTrue(selector.selectStrategy(req) instanceof FastestRouteStrategy);
    }

    @Test
    void testHardConstraintBudgetFiltering() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 1000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertNotEquals("R1_Fast", decision.selectedRoute().id());
    }

    @Test
    void testHardConstraintDeadlineFiltering() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 20, false);
        RouteDecision decision = planner.plan(routes, req);
        assertNotEquals("R2_Cheap", decision.selectedRoute().id());
    }

    @Test
    void testNoValidRouteThrowsException() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 10, 1, false);
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(routes, req));
    }

    @Test
    void testInvalidBalancedWeightsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new BalancedRouteStrategy(0.5, 0.5, 0.5, 0.5));
    }

    @Test
    void testUnavailableRouteIsIgnored() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        RouteDecision decision = planner.plan(routes, req);
        assertNotEquals("R5_Closed", decision.selectedRoute().id());
    }

    @Test
    void testComparisonModeDoesNotFail() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        assertDoesNotThrow(() -> planner.plan(routes, req));
        planner.setStrategy(new EcoFriendlyRouteStrategy());
        assertDoesNotThrow(() -> planner.plan(routes, req));
    }

    @Test
    void testBoundaryCaseZeroBudget() {
        RoutePlanner planner = new RoutePlanner(new CheapestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 0, 60, false);
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(routes, req));
    }

    @Test
    void testEmptyRouteListFails() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest req = new DeliveryRequest(DeliveryPriority.STANDARD, 5000, 60, false);
        assertThrows(NoSuitableRouteException.class, () -> planner.plan(List.of(), req));
    }
}