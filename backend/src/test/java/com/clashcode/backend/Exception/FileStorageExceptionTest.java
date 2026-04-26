package com.clashcode.backend.Exception;

import com.clashcode.backend.exception.FileStorageException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageExceptionTest {

    @Test
    void constructor_WithMessage_SetsMessage() {
        FileStorageException ex = new FileStorageException("Failed to store file");
        assertEquals("Failed to store file", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void constructor_WithMessageAndCause_SetsMessageAndCause() {
        Throwable cause = new RuntimeException("IO error");
        FileStorageException ex = new FileStorageException("Upload failed", cause);

        assertEquals("Upload failed", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
