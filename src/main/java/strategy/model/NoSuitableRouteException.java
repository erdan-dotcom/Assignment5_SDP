package strategy.model;

public class NoSuitableRouteException extends RuntimeException {
    public NoSuitableRouteException(String message) {
        super(message);
    }
}