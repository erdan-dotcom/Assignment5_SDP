package strategy.strategies;

import strategy.core.RouteStrategy;
import strategy.model.DeliveryRequest;
import strategy.model.DeliveryPriority;
import strategy.model.NoSuitableRouteException;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.Comparator;
import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = routes.stream()
                .filter(Route::isAvailable)
                .filter(r -> calculateCost(r, request) <= request.maxBudget())
                .filter(r -> r.estimatedMinutes() <= request.maxDeliveryMinutes())
                .toList();

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("CheapestRouteStrategy: No routes within budget and time limits.");
        }

        Route bestRoute = validRoutes.stream()
                .min(Comparator.comparingDouble(r -> calculateCost(r, request)))
                .orElseThrow();

        double finalCost = calculateCost(bestRoute, request);
        String explanation = String.format("Selected %s. Reason: Lowest cost calculated as %.2f.", bestRoute.id(), finalCost);

        return new RouteDecision(bestRoute, getStrategyName(), explanation);
    }

    private double calculateCost(Route route, DeliveryRequest request) {
        double distanceCost = route.distanceKm() * 5.0;
        double surcharge = request.priority() == DeliveryPriority.URGENT ? 1000.0 : 0.0;
        return route.baseCost() + distanceCost + surcharge;
    }

    @Override
    public String getStrategyName() {
        return "CHEAPEST";
    }
}