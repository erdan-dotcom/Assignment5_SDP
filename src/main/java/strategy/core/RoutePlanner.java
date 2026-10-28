package strategy.core;

import strategy.model.DeliveryRequest;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.List;

public class RoutePlanner {
    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public RouteDecision plan(List<Route> routes, DeliveryRequest request) {
        return strategy.selectRoute(routes, request);
    }
}