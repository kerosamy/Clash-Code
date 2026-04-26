package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.Solution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    @Test
    void testSolutionGettersAndSetters() {
        Solution solution = new Solution();

        solution.setSolutionCode("public class Solution {}");
        solution.setLanguageVersion(LanguageVersion.JAVA_OPENJDK_13);

        assertEquals("public class Solution {}", solution.getSolutionCode());
        assertEquals(LanguageVersion.JAVA_OPENJDK_13, solution.getLanguageVersion());
    }

    @Test
    void testSolutionBuilder() {
        Solution solution = Solution.builder()
                .solutionCode("def solve():")
                .languageVersion(LanguageVersion.PYTHON_3_8)
                .build();

        assertEquals("def solve():", solution.getSolutionCode());
        assertEquals(LanguageVersion.PYTHON_3_8, solution.getLanguageVersion());
    }

    @Test
    void testSolutionNoArgsConstructor() {
        Solution solution = new Solution();
        assertNotNull(solution);
    }

    @Test
    void testSolutionAllArgsConstructor() {
        Solution solution = new Solution("int main() {}", LanguageVersion.CPP_CLANG_7_0);

        assertEquals("int main() {}", solution.getSolutionCode());
        assertEquals(LanguageVersion.CPP_CLANG_7_0, solution.getLanguageVersion());
    }
}

