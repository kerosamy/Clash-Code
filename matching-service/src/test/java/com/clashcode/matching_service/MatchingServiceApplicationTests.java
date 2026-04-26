package com.clashcode.matching_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MatchingServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void main_should_run_without_exception() {
		String[] args = {};
		MatchingServiceApplication.main(args);
		// No assertion needed, test passes if no exception is thrown
	}

}
