package com.clashcode.backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}


// run this to add problem to your database
//	@Bean
//	public CommandLineRunner loadInitialData(ProblemRepository problemRepository) {
//		return args -> {
//			Problem problem1 = new Problem();
//			List<TestCase> testCases = new ArrayList<>();
//			problem1.setTitle("Add Two Integers");
//			problem1.setInputFormat("Two integers a and b.");
//			problem1.setOutputFormat("Print a + b.");
//			problem1.setStatement("Given two integers a and b, print their sum.");
//			problem1.setNotes("a and b can be negative.");
//			problem1.setJudge0Language(Judge0Language.CPP_GCC_9_2_0);
//			problem1.setMainSolution("#include <bits/stdc++.h>\nusing namespace std;\nint main() {\n    int a, b;\n    cin >> a >> b;\n    cout << a + b;\n    return 0;\n}\n");
//			problem1.setTimeLimit(1);
//			problem1.setMemoryLimit(64);
//			problem1.setRate(ProblemRate.RATE_200);
//			problem1.setTags(Arrays.asList(ProblemTags.MATH, ProblemTags.IMPLEMENTATION));
//			TestCase tc1 = new TestCase();
//			tc1.setInput("2 3");
//			tc1.setVisible(true);
//			tc1.setProblem(problem1);
//
//			TestCase tc2 = new TestCase();
//			tc2.setInput("-5 10");
//			tc2.setVisible(true);
//			tc2.setProblem(problem1);
//
//			TestCase tc3 = new TestCase();
//			tc3.setInput("0 0");
//			tc3.setVisible(false);
//			tc3.setProblem(problem1);
//
//			TestCase tc4 = new TestCase();
//			tc4.setInput("1000000 2000000");
//			tc4.setVisible(false);
//			tc4.setProblem(problem1);
//
//			problem1.setTestCases(Arrays.asList(tc1, tc2, tc3, tc4));
//			problemRepository.save(problem1);
//
//			// Add more problems as needed...
//		};
//	}


}






