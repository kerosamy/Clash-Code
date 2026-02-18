package com.clashcode.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface TestCasesStorageService {

    public String storeTestCase(MultipartFile file,
                                Long problemId,
                                Long testCaseId);

    public String getTestCaseContent(String testCasePathStr);

    public String storeTestCaseOutput(String content, Long problemId, Long testCaseId);

    public void deleteTestCasesDirectory(Long problemId);
}
