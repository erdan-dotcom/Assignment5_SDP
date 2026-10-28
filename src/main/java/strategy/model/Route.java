package strategy.model;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double baseCost,
        double carbonEmission,
        int trafficPenaltyMinutes,
        boolean isAvailable,
        int safetyScore
) {}