package com.clashcode.backend.Model;

import com.clashcode.backend.model.ProblemReview;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProblemReviewTest {

    @Test
    void testProblemReviewGettersAndSetters() {
        ProblemReview review = new ProblemReview();
        LocalDateTime now = LocalDateTime.now();

        review.setId(1L);
        review.setProblemId(10L);
        review.setNote("Needs improvement");
        review.setReviewedAt(now);

        assertEquals(1L, review.getId());
        assertEquals(10L, review.getProblemId());
        assertEquals("Needs improvement", review.getNote());
        assertEquals(now, review.getReviewedAt());
    }

    @Test
    void testProblemReviewBuilder() {
        LocalDateTime now = LocalDateTime.now();

        ProblemReview review = ProblemReview.builder()
                .id(2L)
                .problemId(20L)
                .note("Approved")
                .reviewedAt(now)
                .build();

        assertEquals(2L, review.getId());
        assertEquals(20L, review.getProblemId());
        assertEquals("Approved", review.getNote());
        assertEquals(now, review.getReviewedAt());
    }

    @Test
    void testProblemReviewNoArgsConstructor() {
        ProblemReview review = new ProblemReview();
        assertNotNull(review);
    }

    @Test
    void testProblemReviewAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        ProblemReview review = new ProblemReview(1L, 10L, "Note", now);

        assertEquals(1L, review.getId());
        assertEquals(10L, review.getProblemId());
        assertEquals("Note", review.getNote());
        assertEquals(now, review.getReviewedAt());
    }
}
