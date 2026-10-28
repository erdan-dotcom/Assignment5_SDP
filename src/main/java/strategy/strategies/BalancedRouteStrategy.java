package strategy.strategies;

import strategy.core.RouteStrategy;
import strategy.model.DeliveryRequest;
import strategy.model.NoSuitableRouteException;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.Comparator;
import java.util.List;

public class BalancedRouteStrategy implements RouteStrategy {
    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        if (Math.abs(timeWeight + costWeight + distanceWeight + emissionWeight - 1.0) > 0.001) {
            throw new IllegalArgumentException("Weights must sum to 1.0");
        }
        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        List<Route> validRoutes = routes.stream()
                .filter(Route::isAvailable)
                .filter(r -> r.baseCost() <= request.maxBudget())
                .filter(r -> r.estimatedMinutes() <= request.maxDeliveryMinutes())
                .toList();

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("BalancedRouteStrategy: No available routes.");
        }

        double maxTime = validRoutes.stream().mapToDouble(Route::estimatedMinutes).max().orElse(1.0);
        double maxCost = validRoutes.stream().mapToDouble(Route::baseCost).max().orElse(1.0);
        double maxDistance = validRoutes.stream().mapToDouble(Route::distanceKm).max().orElse(1.0);
        double maxEmission = validRoutes.stream().mapToDouble(Route::carbonEmission).max().orElse(1.0);

        Route bestRoute = validRoutes.stream()
                .min(Comparator.comparingDouble(r -> calculateScore(r, maxTime, maxCost, maxDistance, maxEmission)))
                .orElseThrow();

        double score = calculateScore(bestRoute, maxTime, maxCost, maxDistance, maxEmission);
        String explanation = String.format("Selected %s. Reason: Optimal balanced score %.4f.", bestRoute.id(), score);

        return new RouteDecision(bestRoute, getStrategyName(), explanation);
    }

    private double calculateScore(Route r, double maxTime, double maxCost, double maxDist, double maxEmiss) {
        double nTime = r.estimatedMinutes() / (maxTime > 0 ? maxTime : 1);
        double nCost = r.baseCost() / (maxCost > 0 ? maxCost : 1);
        double nDist = r.distanceKm() / (maxDist > 0 ? maxDist : 1);
        double nEmiss = r.carbonEmission() / (maxEmiss > 0 ? maxEmiss : 1);
        return (nTime * timeWeight) + (nCost * costWeight) + (nDist * distanceWeight) + (nEmiss * emissionWeight);
    }

    @Override
    public String getStrategyName() {
        return "BALANCED";
    }
}