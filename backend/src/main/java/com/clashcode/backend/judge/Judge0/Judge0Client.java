package com.clashcode.backend.judge.Judge0;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.judge.CodeExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class Judge0Client implements CodeExecutor {
    private final String JUDGE0_URL = "http://localhost:2358";
    private final String JUDGE0_SUBMIT_URL = "/submissions/?base64_encoded=false&wait=false";
    private final Judge0Mapper mapper = new Judge0Mapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<ExecutionResultDto> executeBatch(String sourceCode,
                                                 String language,
                                                 List<String> testCases,
                                                 List<String> expectedResults){
        List<ExecutionResultDto> results = new ArrayList<>();
        for (int i = 0; i < testCases.size(); i++) {
            Judge0TokenDto judge0TokenDto = submitCode(sourceCode,
                                            testCases.get(i),
                                            language,
                                            expectedResults.get(i));

            Judge0ResponseDto dto = waitForResult(judge0TokenDto);
            results.add(mapper.toExecutionResultDto(dto));
        }
        return results;
    };

    public Judge0TokenDto submitCode (String sourceCode,
                               String testCase,
                               String language,
                               String expectedResult){
        System.out.println((mapper.mapToJudge0Id(LanguageVersion.valueOf(language))));
        Judge0RequestDto requestDto = Judge0RequestDto.builder()
                                    .sourceCode(sourceCode)
                                    .stdin(testCase)
                                    .languageId(mapper.mapToJudge0Id(LanguageVersion.valueOf(language)))
                                    .build();
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
        }
    }
    public Judge0ResponseDto waitForResult(Judge0TokenDto token) {
        String url = JUDGE0_URL + "/submissions/" + token.getToken() + "?base64_encoded=false";

        while (true) {
            ResponseEntity<Judge0ResponseDto> response =
                    restTemplate.getForEntity(url, Judge0ResponseDto.class);

            Judge0ResponseDto body = response.getBody();

            if (body == null || body.getStatus() == null) {
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                continue;
            }

            int statusId = body.getStatus().getId();

            if (statusId != 1 && statusId != 2) {
                return body;
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}
        }
    }

}
