package strategy.model;

public record RouteDecision(
        Route selectedRoute,
        String strategyName,
        String explanation
) {}