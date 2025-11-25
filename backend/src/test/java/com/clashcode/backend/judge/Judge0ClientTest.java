package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.judge.Judge0.Judge0ResponseDto;
import com.clashcode.backend.judge.Judge0.Judge0TokenDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Judge0ClientTest {

    @Test
    void testJudge0Execution() {
        Judge0Client client = new Judge0Client();

        String code =
                """
                #include <stdio.h>
                int main(){
                    int a;
                    scanf("%d",&a);
                    printf("%d", a*2);
                    return 0;
                }
                """;

        String testCase = "7";
        String language = "CPP_GCC_9_2"; // must match your enum
        String expected = "14";

        // 1) submit job
        System.out.println("?????");
        Judge0TokenDto token = client.submitCode(code, testCase, language, expected);
        System.out.println(token.getToken());
        assertNotNull(token);
        assertNotNull(token.getToken());

        // 2) wait for result
        Judge0ResponseDto result = client.waitForResult(token);
        assertNotNull(result);
        System.out.println(result.getTime());
        System.out.println(result.getMemory());
        System.out.println(result.getStderr());
        System.out.println("Output = " + result.getStdout());
        System.out.println("Status = " + result.getStatus().getDescription());

        assertEquals("14", result.getStdout().trim());
        assertEquals("Accepted", result.getStatus().getDescription());
    }

    @Test
    void testJudge0ExecutionMulti() {
        Judge0Client client = new Judge0Client();

        String code =
                """
                #include <stdio.h>
                int main(){
                    int a;
                    scanf("%d",&a);
                    printf("%d", a*2);
                    return 0;
                }
                """;

        String language = "CPP_GCC_9_2";

        List<String> testCases = List.of("7", "8", "9", "10");
        List<String> expectedResults = List.of("14", "16", "18", "20"); // doubled values

        List<ExecutionResultDto> results = client.executeBatch(code, language, testCases, expectedResults);

        for (int i = 0; i < results.size(); i++) {
            ExecutionResultDto r = results.get(i);
            System.out.println("Test case: " + testCases.get(i));
            System.out.println("Output = " + r.getResult());
            System.out.println("Status = " + r.getStatus());
            System.out.println("time =" + r.getTimeTaken());
            System.out.println("memory =" + r.getMemoryTaken());
            assertEquals(expectedResults.get(i), r.getResult().trim());
            assertEquals("Accepted", r.getStatus());
        }
    }

}
