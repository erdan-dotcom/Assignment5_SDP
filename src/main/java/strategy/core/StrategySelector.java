package strategy.core;

import strategy.model.DeliveryRequest;
import strategy.strategies.*;

public class StrategySelector {
    public RouteStrategy selectStrategy(DeliveryRequest request) {
        return switch (request.priority()) {
            case URGENT -> new FastestRouteStrategy();
            case ECONOMY -> new CheapestRouteStrategy();
            case ECO -> new EcoFriendlyRouteStrategy();
            case PREMIUM -> new SafestRouteStrategy();
            default -> new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        };
    }
}
