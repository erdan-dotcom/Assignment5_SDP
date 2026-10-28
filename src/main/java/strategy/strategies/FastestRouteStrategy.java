package strategy.strategies;

import strategy.core.RouteStrategy;
import strategy.model.DeliveryRequest;
import strategy.model.NoSuitableRouteException;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.Comparator;
import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = routes.stream()
                .filter(Route::isAvailable)
                .filter(r -> calculateTotalTime(r) <= request.maxDeliveryMinutes())
                .filter(r -> r.baseCost() <= request.maxBudget())
                .toList();

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("FastestRouteStrategy: No routes satisfy time and budget constraints.");
        }

        Route bestRoute = validRoutes.stream()
                .min(Comparator.comparingInt(this::calculateTotalTime))
                .orElseThrow();

        String explanation = String.format(
                "Selected %s. Reason: Base time %d min + Traffic penalty %d min. Total: %d min.",
                bestRoute.id(), bestRoute.estimatedMinutes(), bestRoute.trafficPenaltyMinutes(), calculateTotalTime(bestRoute)
        );

        return new RouteDecision(bestRoute, getStrategyName(), explanation);
    }

    private int calculateTotalTime(Route route) {
        return route.estimatedMinutes() + route.trafficPenaltyMinutes();
    }

    @Override
    public String getStrategyName() {
        return "FASTEST";
    }
}