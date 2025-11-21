package com.clashcode.backend.config;

import com.clashcode.backend.enums.Judge0Language;
import com.clashcode.backend.enums.ProblemRate;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
@Configuration
public class ProblemLoader {
    @Bean
    public CommandLineRunner loadInitialData(ProblemRepository problemRepository) {
        if(problemRepository.count() != 0) {
            return args -> {};
        }
        return args -> {
            // Create a problem
            Problem problem1 = new Problem();
            problem1.setTitle("Add Two Integers");
            problem1.setInputFormat("Two integers a and b.");
            problem1.setOutputFormat("Print a + b.");
            problem1.setStatement("Given two integers a and b, print their sum.");
            problem1.setNotes("a and b can be negative.");
            problem1.setJudge0Language(Judge0Language.CPP_GCC_9_2_0);
            problem1.setMainSolution("""
                    #include <bits/stdc++.h>
                    using namespace std;
                    int main() {
                        int a, b;
                        cin >> a >> b;
                        cout << a + b;
                        return 0;
                    }
                    """);
            problem1.setTimeLimit(1000);       // in ms
            problem1.setMemoryLimit(64);    // in MB
            problem1.setRate(ProblemRate.RATE_200);
            problem1.setTags(Arrays.asList(ProblemTags.MATH, ProblemTags.IMPLEMENTATION));

            // Create test cases
            TestCase tc1 = new TestCase();
            tc1.setInput("2 3");
            tc1.setVisible(true);
            tc1.setProblem(problem1);

            TestCase tc2 = new TestCase();
            tc2.setInput("-5 10");
            tc2.setVisible(true);
            tc2.setProblem(problem1);

            TestCase tc3 = new TestCase();
            tc3.setInput("0 0");
            tc3.setVisible(false);
            tc3.setProblem(problem1);

            TestCase tc4 = new TestCase();
            tc4.setInput("1000000 2000000");
            tc4.setVisible(false);
            tc4.setProblem(problem1);

            // Attach test cases to problem
            problem1.setTestCases(Arrays.asList(tc1, tc2, tc3, tc4));

            // Save to database
            problemRepository.save(problem1);

            System.out.println("Initial problem data inserted into database!");
        };
    }
}
