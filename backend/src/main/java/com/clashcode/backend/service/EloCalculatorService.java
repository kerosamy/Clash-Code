package com.clashcode.backend.service;

public class EloCalculatorService {
    private static final double BASE_K = 50;

    public static double calculateExpectedScore(double ratingA, double ratingB) {
        return 1.0 / (1.0 + Math.pow(10, (ratingB - ratingA) / 400.0));
    }

    public static int calculateNewRating(int currentRating, double expectedScore, double actualScore, int difficulty) {
        double difficultyMultiplier = Math.max(0.5, Math.min(2.0, difficulty / 1000.0));
        double kFactor = BASE_K * difficultyMultiplier;

        int newRating = (int) Math.round(currentRating + kFactor * (actualScore - expectedScore));

        return Math.max(0, newRating);
    }
}