package strategy.strategies;

import strategy.core.RouteStrategy;
import strategy.model.DeliveryRequest;
import strategy.model.NoSuitableRouteException;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.Comparator;
import java.util.List;

public class SafestRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = routes.stream()
                .filter(Route::isAvailable)
                .filter(r -> r.baseCost() <= request.maxBudget())
                .filter(r -> r.estimatedMinutes() <= request.maxDeliveryMinutes())
                .toList();

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("SafestRouteStrategy: No available routes.");
        }

        Route bestRoute = validRoutes.stream()
                .max(Comparator.comparingInt(Route::safetyScore))
                .orElseThrow();

        String explanation = String.format("Selected %s. Reason: Highest safety score of %d.", bestRoute.id(), bestRoute.safetyScore());
        return new RouteDecision(bestRoute, getStrategyName(), explanation);
    }

    @Override
    public String getStrategyName() {
        return "SAFEST";
    }
}