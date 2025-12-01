package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Judge0ClientTest {

    private final Judge0Client client = new Judge0Client();

    @Test
    void testAccepted() {
        String code = """
                #include <stdio.h>
                int main(){
                    int a;
                    scanf("%d",&a);
                    printf("%d", a*2);
                    return 0;
                }
                """;
        String input = "7";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 2000;
        Integer memoryLimit = 128;

        String output = client.executeAndReturnOutput(input, code, language, timeLimit, memoryLimit);
        System.out.println("Accepted Test:");
        System.out.println("Output = " + output.trim());
        assertEquals("14", output.trim());
    }

    @Test
    void testWrongAnswer() {
        String code = """
                #include <stdio.h>
                int main(){
                    int a;
                    scanf("%d",&a);
                    printf("%d", a+1);
                    return 0;
                }
                """;
        String input = "7";
        String expectedOutput = "14";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 2000;
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of(expectedOutput),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Wrong Answer Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertEquals("Wrong Answer", r.getStatus());
    }

    @Test
    void testCompilationError() {
        String code = """
                #include <stdio.h>
                int main() {
                    prinf("Hello");
                    return 0;
                }
                """;
        String input = "";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 2000;
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of(""),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Compilation Error Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertTrue( r.getStatus().contains("Compilation Error"));
    }

    @Test
    void testRuntimeError() {
        String code = """
                #include <stdio.h>
                int main() {
                    int a = 0;
                    int b = 5 / a; // division by zero
                    printf("%d", b);
                    return 0;
                }
                """;
        String input = "";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 2000;
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of(""),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Runtime Error Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertTrue( r.getStatus().contains("Runtime Error"));
    }

    @Test
    void testTimeLimitExceeded() {
        String code = """
                int main(){
                    while(1){} // infinite loop
                    return 0;
                }
                """;
        String input = "";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 1000; // 1 second CPU
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of(""),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Time Limit Exceeded Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertEquals("Time Limit Exceeded", r.getStatus());
    }


    @Test
    void testTimeLimitExceeded2() {
        String code = """
                 #include <iostream>
                 using namespace std;
                int main(){
                int a = 0;
                   for(int i = 0 ; i < 1000000000 ; i++){
                    a++;
                   }
                   cout<<a; 
                   return 0;
                }
                """;
        String input = "";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 1000; // 1 second CPU
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of("1000000000"),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Time Limit Exceeded Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertEquals("Time Limit Exceeded", r.getStatus());
    }

    @Test
    void testAccepted2() {
        String code = """
                #include <iostream>
                using namespace std;
                int main(){
                int a = 0;
                cout << 2 << endl;
                cout << 1 << ' ' << 2 << endl;
                return 0;
                }
                """;
        String input = "";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 1000; // 1 second CPU
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of("2\n1 2\n"),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Time Limit Exceeded Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertEquals("Accepted", r.getStatus());
    }

    @Test
    void testAccepted3() {
        String code = """
                 #include <iostream>
                 using namespace std;
                int main(){
                int a = 0;
                   for(int i = 0 ; i < 1000000000 ; i++){
                    a++;
                   }
                   cout<<a; 
                   return 0;
                }
                """;
        String input = "";
        String language = "CPP_GCC_9_2";
        Integer timeLimit = 10000; // 1 second CPU
        Integer memoryLimit = 128;

        List<ExecutionResultDto> results = client.executeBatch(
                code,
                language,
                List.of(input),
                List.of("1000000000"),
                timeLimit,
                memoryLimit
        );

        ExecutionResultDto r = results.get(0);
        System.out.println("Time Limit Exceeded Test:");
        System.out.println("Output = " + r.getResult());
        System.out.println("Status = " + r.getStatus());
        System.out.println("Time = " + r.getTimeTaken());
        System.out.println("Memory = " + r.getMemoryTaken());
        assertEquals("Accepted", r.getStatus());
    }


}
