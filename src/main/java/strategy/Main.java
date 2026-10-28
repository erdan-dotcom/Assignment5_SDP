package strategy;

import strategy.core.RoutePlanner;
import strategy.core.StrategySelector;
import strategy.model.DeliveryPriority;
import strategy.model.DeliveryRequest;
import strategy.model.Location;
import strategy.model.Route;
import strategy.model.RouteDecision;
import strategy.strategies.BalancedRouteStrategy;
import strategy.strategies.CheapestRouteStrategy;
import strategy.strategies.EcoFriendlyRouteStrategy;
import strategy.strategies.FastestRouteStrategy;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Location locA = new Location("Warehouse", 51.16, 71.43);
        Location locB = new Location("Client", 51.12, 71.40);

        List<Route> routes = List.of(
                new Route("Route_A_Highway", locA, locB, 15.0, 20, 1500, 4.5, 15, true, 80),
                new Route("Route_B_City", locA, locB, 10.0, 35, 1000, 2.1, 5, true, 90),
                new Route("Route_C_Eco", locA, locB, 12.0, 40, 1200, 1.1, 2, true, 95),
                new Route("Route_D_Cheap", locA, locB, 18.0, 50, 800, 3.5, 0, true, 70),
                new Route("Route_E_Closed", locA, locB, 8.0, 15, 2000, 2.0, 0, false, 50),
                new Route("Route_F_Premium", locA, locB, 14.0, 25, 5000, 3.0, 0, true, 100)
        );

        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 6000, 60, false);
        RoutePlanner planner = new RoutePlanner(new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));

        System.out.println("=== STRATEGY COMPARISON MODE ===");
        System.out.println("DELIVERY REQUEST: " + request.priority() + " | Budget: " + request.maxBudget() + " | Max Time: " + request.maxDeliveryMinutes());

        planner.setStrategy(new FastestRouteStrategy());
        printDecision(planner.plan(routes, request));

        planner.setStrategy(new CheapestRouteStrategy());
        printDecision(planner.plan(routes, request));

        planner.setStrategy(new EcoFriendlyRouteStrategy());
        printDecision(planner.plan(routes, request));

        planner.setStrategy(new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2));
        printDecision(planner.plan(routes, request));

        System.out.println("\n=== AUTOMATIC STRATEGY SELECTION ===");
        StrategySelector selector = new StrategySelector();
        DeliveryRequest urgentReq = new DeliveryRequest(DeliveryPriority.URGENT, 10000, 30, false);
        planner.setStrategy(selector.selectStrategy(urgentReq));
        printDecision(planner.plan(routes, urgentReq));
    }

    private static void printDecision(RouteDecision decision) {
        System.out.println(decision.strategyName() + " Strategy:");
        System.out.println("Selected: " + decision.selectedRoute().id());
        System.out.println("Reason: " + decision.explanation() + "\n");
    }
}