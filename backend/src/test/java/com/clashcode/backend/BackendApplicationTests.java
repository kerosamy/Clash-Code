package com.clashcode.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BackendApplicationTest {
	@Test
	void main_shouldStartApplicationWithoutErrors() {
		assertDoesNotThrow(() -> BackendApplication.main(new String[]{}));
	}
}