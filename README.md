# Smart Delivery & Route Optimization Engine (Strategy Pattern)

## Architecture & UML Diagrams

### Class Diagram
```mermaid
classDiagram
    class RoutePlanner {
        -RouteStrategy strategy
        +setStrategy(RouteStrategy)
        +plan(List~Route~, DeliveryRequest) RouteDecision
    }
    
    class StrategySelector {
        +selectStrategy(DeliveryRequest) RouteStrategy
    }
    
    class RouteStrategy {
        <<interface>>
        +selectRoute(List~Route~, DeliveryRequest) RouteDecision
        +getStrategyName() String
    }
    
    class FastestRouteStrategy
    class CheapestRouteStrategy
    class EcoFriendlyRouteStrategy
    class BalancedRouteStrategy
    class SafestRouteStrategy
    
    RoutePlanner o--> RouteStrategy : uses
    StrategySelector ..> RouteStrategy : creates
    RouteStrategy <|.. FastestRouteStrategy
    RouteStrategy <|.. CheapestRouteStrategy
    RouteStrategy <|.. EcoFriendlyRouteStrategy
    RouteStrategy <|.. BalancedRouteStrategy
    RouteStrategy <|.. SafestRouteStrategy

sequenceDiagram
    participant Client as Main/Client
    participant Selector as StrategySelector
    participant Planner as RoutePlanner
    participant Strategy as FastestRouteStrategy
    
    Client->>Selector: selectStrategy(request)
    Selector-->>Client: returns FastestRouteStrategy
    Client->>Planner: setStrategy(FastestRouteStrategy)
    Client->>Planner: plan(routes, request)
    Planner->>Strategy: selectRoute(routes, request)
    Strategy-->>Planner: returns RouteDecision
    Planner-->>Client: returns RouteDecision