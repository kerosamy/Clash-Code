package com.clashcode.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EloCalculatorServiceTest {

    @Test
    void testExpectedScoreSymmetry() {
        double ratingA = 1600;
        double ratingB = 1400;

        double expectedScoreA = EloCalculatorService.calculateExpectedScore(ratingA, ratingB);
        double expectedScoreB = EloCalculatorService.calculateExpectedScore(ratingB, ratingA);

        // Expected scores sum should be close to 1
        assertEquals(1.0, expectedScoreA + expectedScoreB, 0.0001);

        // Stronger player should have higher expected score
        assertTrue(expectedScoreA > expectedScoreB);
    }

    @Test
    void testNewRatingAfterWin() {
        int currentRating = 1500;
        int difficulty = 500; // mid-level problem
        double expectedScore = EloCalculatorService.calculateExpectedScore(currentRating, 1500);

        int newRating = EloCalculatorService.calculateNewRating(currentRating, expectedScore, 1.0, difficulty);

        assertTrue(newRating > currentRating, "Rating should increase after a win");
    }

    @Test
    void testNewRatingAfterDrawAgainstStrongerOpponent() {
        int currentRating = 1500;
        int opponentRating = 1600;
        int difficulty = 800; // high difficulty problem

        double expectedScore = EloCalculatorService.calculateExpectedScore(currentRating, opponentRating);
        int newRating = EloCalculatorService.calculateNewRating(currentRating, expectedScore, 0.5, difficulty);

        // Rating should slightly increase because underdog drew
        assertTrue(newRating > currentRating, "Rating should increase for underdog after draw");
    }

    @Test
    void testNewRatingAfterLossAgainstWeakerOpponent() {
        int currentRating = 1600;
        int opponentRating = 1500;
        int difficulty = 600;

        double expectedScore = EloCalculatorService.calculateExpectedScore(currentRating, opponentRating);
        int newRating = EloCalculatorService.calculateNewRating(currentRating, expectedScore, 0.0, difficulty);

        // Rating should decrease after losing to weaker opponent
        assertTrue(newRating < currentRating, "Rating should decrease after losing to weaker opponent");
    }

    @Test
    void testDifficultyMultiplierBounds() {
        int currentRating = 1500;

        // Very low difficulty (should cap at 0.5)
        int newRatingLow = EloCalculatorService.calculateNewRating(currentRating, 0.5, 1.0, 100);
        int newRatingHigh = EloCalculatorService.calculateNewRating(currentRating, 0.5, 1.0, 5000); // should cap at 2.0

        assertTrue(newRatingHigh > newRatingLow, "High difficulty should increase rating more than low difficulty");
    }
}
