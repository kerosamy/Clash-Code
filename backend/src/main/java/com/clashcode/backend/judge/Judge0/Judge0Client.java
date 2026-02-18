package com.clashcode.backend.judge.Judge0;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.judge.CodeExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class Judge0Client implements CodeExecutor {
    @Value("${clashcode.judge0.url}")
    private String JUDGE0_URL ;
    private final String JUDGE0_SUBMIT_URL = "/submissions/?base64_encoded=false&wait=false";
    private final Judge0Mapper mapper = new Judge0Mapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ExecutionResultDto executeAndCompare(
        String sourceCode,
        String language,
        String testCase,
        String expectedResult,
        Integer timeLimit,
        Integer memoryLimit
    ) {
        Judge0TokenDto judge0TokenDto = submitCode(
                                            sourceCode,
                                            testCase,
                                            language,
                                            expectedResult,
                                            timeLimit,
                                            memoryLimit
                                        );

        Judge0ResponseDto dto = waitForResult(judge0TokenDto);
        return  mapper.toExecutionResultDto(dto);
    };
    @Override
    public String executeAndReturnOutput(
            String stdin,
            String sourceCode,
            String language,
            Integer timeLimit,
            Integer memoryLimit
    ) {
        Judge0TokenDto token = submitCode(
                sourceCode,
                stdin,
                language,
                null,
                timeLimit,
                memoryLimit);

        Judge0ResponseDto result = waitForResult(token);
        if (result.getStdout() != null) {
            return result.getStdout();
        }
        if (result.getCompileOutput() != null) {
            return "Compilation Error:\n" + result.getCompileOutput();
        }
        if (result.getStderr() != null) {
            return "Runtime Error:\n" + result.getStderr();
        }
        return "Unknown error occurred";
    }


    public Judge0TokenDto submitCode (
            String sourceCode,
            String testCase,
            String language,
            String expectedResult,
            Integer timeLimit,
            Integer memoryLimit
    ){
        Judge0RequestDto requestDto = mapper.toRequestDto(
                sourceCode,
                testCase,
                language,
                expectedResult,
                timeLimit,
                memoryLimit
        );
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestDto);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<Judge0TokenDto> response =
                    restTemplate.exchange(JUDGE0_URL + JUDGE0_SUBMIT_URL,
                            HttpMethod.POST,
                            entity,
                            Judge0TokenDto.class);

            return response.getBody();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize request", e);
        } catch (org.springframework.web.client.ResourceAccessException ex) {
            throw new RuntimeException("Judge service is unavailable (connection error).", ex);
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            throw new RuntimeException("Judge service returned error: "
                    + ex.getStatusCode(), ex);
        } catch (RestClientException ex) {
            throw new RuntimeException("Unexpected error while contacting Judge service.", ex);
        }
    }

    public Judge0ResponseDto waitForResult(Judge0TokenDto token) {
        String url = JUDGE0_URL + "/submissions/" + token.getToken() + "?base64_encoded=true";
        int maxRetries = 50;       // Maximum number of polls
        long delayMillis = 300;    // Delay between polls
        int attempt = 0;

        while (attempt < maxRetries) {
            ResponseEntity<Judge0ResponseDto> response =
                    restTemplate.getForEntity(url, Judge0ResponseDto.class);

            Judge0ResponseDto body = response.getBody();

            if (body != null && body.getStatus() != null) {
                int statusId = body.getStatus().getId();
                if (statusId != 1 && statusId != 2) {

                    if (body.getStdout() != null)
                        body.setStdout(mapper.decode(body.getStdout()));

                    if (body.getStderr() != null)
                        body.setStderr(mapper.decode(body.getStderr()));

                    if (body.getCompileOutput() != null)
                        body.setCompileOutput(mapper.decode(body.getCompileOutput()));

                    return body;
                }
            }
            attempt++;
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Respect interruption
                throw new RuntimeException("Polling interrupted", e);
            }
        }
        throw new RuntimeException("Timeout waiting for Judge0 result after " + (maxRetries * delayMillis) + " ms");
    }


    public boolean isJudgeAvailable() {
        try {
            ResponseEntity<String> response =
                    restTemplate.getForEntity(JUDGE0_URL + "/languages", String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

}
