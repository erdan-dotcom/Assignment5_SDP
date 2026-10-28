package strategy.strategies;

import strategy.core.RouteStrategy;
import strategy.model.DeliveryRequest;
import strategy.model.NoSuitableRouteException;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.Comparator;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = routes.stream()
                .filter(Route::isAvailable)
                .filter(r -> r.baseCost() <= request.maxBudget())
                .filter(r -> r.estimatedMinutes() <= request.maxDeliveryMinutes())
                .toList();

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("EcoFriendlyRouteStrategy: No available routes within constraints.");
        }

        Route bestRoute = validRoutes.stream()
                .min(Comparator.comparingDouble(this::calculateEcoScore))
                .orElseThrow();

        String explanation = String.format("Selected %s. Reason: Eco score %.2f based on CO2 and distance.", bestRoute.id(), calculateEcoScore(bestRoute));
        return new RouteDecision(bestRoute, getStrategyName(), explanation);
    }

    private double calculateEcoScore(Route route) {
        return (route.carbonEmission() * 0.7) + (route.distanceKm() * 0.3);
    }

    @Override
    public String getStrategyName() {
        return "ECO_FRIENDLY";
    }
}