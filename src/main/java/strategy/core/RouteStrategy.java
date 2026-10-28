package strategy.core;

import strategy.model.DeliveryRequest;
import strategy.model.Route;
import strategy.model.RouteDecision;
import java.util.List;

public interface RouteStrategy {
    RouteDecision selectRoute(List<Route> routes, DeliveryRequest request);
    String getStrategyName();
}