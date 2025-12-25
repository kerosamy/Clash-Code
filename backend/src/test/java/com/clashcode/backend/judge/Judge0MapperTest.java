package com.clashcode.backend.judge;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.judge.Judge0.Judge0Mapper;
import com.clashcode.backend.judge.Judge0.Judge0RequestDto;
import com.clashcode.backend.judge.Judge0.Judge0ResponseDto;
import com.clashcode.backend.judge.Judge0.Judge0StatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Judge0MapperTest {

    private Judge0Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new Judge0Mapper();
    }

    // ========== Language Mapping Tests ==========

    @Test
    void test_mapToJudge0Id_python() {
        assertEquals(71, mapper.mapToJudge0Id(LanguageVersion.PYTHON_3_8));
        assertEquals(70, mapper.mapToJudge0Id(LanguageVersion.PYTHON_2_7));
    }

    @Test
    void test_mapToJudge0Id_c() {
        assertEquals(48, mapper.mapToJudge0Id(LanguageVersion.C_GCC_7_4));
        assertEquals(49, mapper.mapToJudge0Id(LanguageVersion.C_GCC_8_3));
        assertEquals(50, mapper.mapToJudge0Id(LanguageVersion.C_GCC_9_2));
        assertEquals(75, mapper.mapToJudge0Id(LanguageVersion.C_CLANG_7_0));
    }

    @Test
    void test_mapToJudge0Id_cpp() {
        assertEquals(52, mapper.mapToJudge0Id(LanguageVersion.CPP_GCC_7_4));
        assertEquals(53, mapper.mapToJudge0Id(LanguageVersion.CPP_GCC_8_3));
        assertEquals(54, mapper.mapToJudge0Id(LanguageVersion.CPP_GCC_9_2));
        assertEquals(76, mapper.mapToJudge0Id(LanguageVersion.CPP_CLANG_7_0));
    }

    @Test
    void test_mapToJudge0Id_java() {
        assertEquals(62, mapper.mapToJudge0Id(LanguageVersion.JAVA_OPENJDK_13));
        assertEquals(63, mapper.mapToJudge0Id(LanguageVersion.JAVA_OPENJDK_11));
    }

    @Test
    void test_mapToJudge0Id_javascript() {
        assertEquals(63, mapper.mapToJudge0Id(LanguageVersion.JAVASCRIPT_NODEJS_12));
    }

    @Test
    void test_mapToJudge0Id_csharp() {
        assertEquals(51, mapper.mapToJudge0Id(LanguageVersion.CSHARP_MONO));
    }

    @Test
    void test_mapToJudge0Id_go() {
        assertEquals(60, mapper.mapToJudge0Id(LanguageVersion.GO));
    }

    @Test
    void test_mapToJudge0Id_kotlin() {
        assertEquals(78, mapper.mapToJudge0Id(LanguageVersion.KOTLIN));
    }

    @Test
    void test_mapToJudge0Id_rust() {
        assertEquals(73, mapper.mapToJudge0Id(LanguageVersion.RUST));
    }

    @Test
    void test_mapToJudge0Id_ruby() {
        assertEquals(72, mapper.mapToJudge0Id(LanguageVersion.RUBY));
    }

    @Test
    void test_mapToJudge0Id_php() {
        assertEquals(68, mapper.mapToJudge0Id(LanguageVersion.PHP));
    }

    @Test
    void test_mapToJudge0Id_lua() {
        assertEquals(64, mapper.mapToJudge0Id(LanguageVersion.LUA));
    }

    @Test
    void test_mapToJudge0Id_swift() {
        assertEquals(83, mapper.mapToJudge0Id(LanguageVersion.SWIFT));
    }

    @Test
    void test_mapToJudge0Id_r() {
        assertEquals(80, mapper.mapToJudge0Id(LanguageVersion.R));
    }

    @Test
    void test_mapToJudge0Id_others() {
        assertEquals(46, mapper.mapToJudge0Id(LanguageVersion.BASH));
        assertEquals(47, mapper.mapToJudge0Id(LanguageVersion.BASIC));
        assertEquals(86, mapper.mapToJudge0Id(LanguageVersion.CLOJURE));
        assertEquals(77, mapper.mapToJudge0Id(LanguageVersion.COBOL));
        assertEquals(55, mapper.mapToJudge0Id(LanguageVersion.COMMON_LISP));
        assertEquals(56, mapper.mapToJudge0Id(LanguageVersion.D));
        assertEquals(57, mapper.mapToJudge0Id(LanguageVersion.ELIXIR));
        assertEquals(58, mapper.mapToJudge0Id(LanguageVersion.ERLANG));
        assertEquals(59, mapper.mapToJudge0Id(LanguageVersion.FORTRAN));
        assertEquals(88, mapper.mapToJudge0Id(LanguageVersion.GROOVY));
        assertEquals(61, mapper.mapToJudge0Id(LanguageVersion.HASKELL));
        assertEquals(65, mapper.mapToJudge0Id(LanguageVersion.OCAML));
        assertEquals(66, mapper.mapToJudge0Id(LanguageVersion.OCTAVE));
        assertEquals(67, mapper.mapToJudge0Id(LanguageVersion.PASCAL));
        assertEquals(85, mapper.mapToJudge0Id(LanguageVersion.PERL));
        assertEquals(69, mapper.mapToJudge0Id(LanguageVersion.PROLOG));
        assertEquals(79, mapper.mapToJudge0Id(LanguageVersion.OBJECTIVEC));
        assertEquals(87, mapper.mapToJudge0Id(LanguageVersion.FSHARP));
        assertEquals(82, mapper.mapToJudge0Id(LanguageVersion.SQL));
        assertEquals(84, mapper.mapToJudge0Id(LanguageVersion.VISUAL_BASIC));
        assertEquals(89, mapper.mapToJudge0Id(LanguageVersion.MULTIFILE_PROGRAM));
        assertEquals(44, mapper.mapToJudge0Id(LanguageVersion.EXECUTABLE));
    }

    // ========== ExecutionResultDto Mapping Tests ==========

    @Test
    void test_toExecutionResultDto_basicConversion() {
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setTime(1.5);
        response.setMemory(2048.0);
        response.setStdout("Hello World");
        response.setStatus(new Judge0StatusDto(3, "Accepted"));

        ExecutionResultDto result = mapper.toExecutionResultDto(response);

        assertEquals(1500, result.getTimeTaken());
        assertEquals(2, result.getMemoryTaken());
        assertEquals("Accepted", result.getStatus());
        assertEquals("Hello World", result.getResult());
    }

    @Test
    void test_toExecutionResultDto_zeroValues() {
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setTime(0.0);
        response.setMemory(0.0);
        response.setStdout("");
        response.setStatus(new Judge0StatusDto(4, "Wrong Answer"));

        ExecutionResultDto result = mapper.toExecutionResultDto(response);

        assertEquals(0, result.getTimeTaken());
        assertEquals(0, result.getMemoryTaken());
        assertEquals("Wrong Answer", result.getStatus());
        assertEquals("", result.getResult());
    }

    @Test
    void test_toExecutionResultDto_largeValues() {
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setTime(10.999);
        response.setMemory(262144.0); // 256 MB in KB
        response.setStdout("Large output");
        response.setStatus(new Judge0StatusDto(3, "Accepted"));

        ExecutionResultDto result = mapper.toExecutionResultDto(response);

        assertEquals(10999, result.getTimeTaken());
        assertEquals(256, result.getMemoryTaken());
        assertEquals("Accepted", result.getStatus());
        assertEquals("Large output", result.getResult());
    }

    @Test
    void test_toExecutionResultDto_nullStdout() {
        Judge0ResponseDto response = new Judge0ResponseDto();
        response.setTime(0.5);
        response.setMemory(1024.0);
        response.setStdout(null);
        response.setStatus(new Judge0StatusDto(5, "Time Limit Exceeded"));

        ExecutionResultDto result = mapper.toExecutionResultDto(response);

        assertNull(result.getResult());
        assertEquals("Time Limit Exceeded", result.getStatus());
    }

    // ========== RequestDto Mapping Tests ==========

    @Test
    void test_toRequestDto_basicConversion() {
        String code = "print('Hello')";
        String input = "input";
        String language = "PYTHON_3_8";
        String expected = "Hello";
        int timeLimitMs = 2000;
        int memoryLimitMb = 128;

        Judge0RequestDto request = mapper.toRequestDto(code, input, language, expected, timeLimitMs, memoryLimitMb);

        assertEquals(code, request.getSourceCode());
        assertEquals(input, request.getStdin());
        assertEquals(71, request.getLanguageId());
        assertEquals(expected, request.getExpectedOutput());
        assertEquals(131072.0, request.getMemoryLimit()); // 128 * 1024
        assertEquals(2.0, request.getTimeLimit());
        assertEquals(4.0, request.getWallTimeLimit()); // 2 * CPU time
    }

    @Test
    void test_toRequestDto_nullExpectedOutput() {
        Judge0RequestDto request = mapper.toRequestDto(
                "code", "input", "JAVA_OPENJDK_11", null, 1000, 64
        );

        assertNull(request.getExpectedOutput());
        assertEquals(65536.0, request.getMemoryLimit()); // 64 * 1024
        assertEquals(1.0, request.getTimeLimit());
        assertEquals(2.0, request.getWallTimeLimit());
    }

    @Test
    void test_toRequestDto_emptyStrings() {
        Judge0RequestDto request = mapper.toRequestDto(
                "", "", "CPP_GCC_9_2", "", 500, 32
        );

        assertEquals("", request.getSourceCode());
        assertEquals("", request.getStdin());
        assertEquals("", request.getExpectedOutput());
        assertEquals(54, request.getLanguageId());
        assertEquals(32768.0, request.getMemoryLimit()); // 32 * 1024
        assertEquals(0.5, request.getTimeLimit());
        assertEquals(1.0, request.getWallTimeLimit());
    }

    @Test
    void test_toRequestDto_largeTimeLimits() {
        Judge0RequestDto request = mapper.toRequestDto(
                "code", "input", "RUST", "output", 10000, 512
        );

        assertEquals(524288.0, request.getMemoryLimit()); // 512 * 1024
        assertEquals(10.0, request.getTimeLimit());
        assertEquals(20.0, request.getWallTimeLimit());
    }

    @Test
    void test_toRequestDto_fractionalTime() {
        Judge0RequestDto request = mapper.toRequestDto(
                "code", "input", "GO", "output", 1500, 256
        );

        assertEquals(262144.0, request.getMemoryLimit()); // 256 * 1024
        assertEquals(1.5, request.getTimeLimit());
        assertEquals(3.0, request.getWallTimeLimit());
    }

    // ========== Decode Tests ==========

    @Test
    void test_decode_validBase64() {
        String encoded = "SGVsbG8gV29ybGQ="; // "Hello World"
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void test_decode_withQuotes() {
        String encoded = "\"SGVsbG8gV29ybGQ=\"";
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void test_decode_withNewlines() {
        String encoded = "SGVsbG8g\nV29ybGQ=";
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void test_decode_withCarriageReturns() {
        String encoded = "SGVsbG8g\rV29ybGQ=";
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void test_decode_withSpaces() {
        String encoded = "SGVsbG8g V29ybGQ=";
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void test_decode_withWhitespaceAndQuotes() {
        String encoded = "\" SGVsbG8gV29ybGQ= \"\n\r";
        String decoded = mapper.decode(encoded);
        assertEquals("Hello World", decoded);
    }

    @Test
    void test_decode_invalidBase64_returnsOriginal() {
        String invalid = "invalid@@base64";
        String decoded = mapper.decode(invalid);
        assertEquals(invalid, decoded);
    }

    @Test
    void test_decode_emptyString() {
        String empty = "";
        String decoded = mapper.decode(empty);
        assertEquals(empty, decoded);
    }

    @Test
    void test_decode_specialCharacters() {
        String encoded = "SGVsbG8sIOS4lueVjCEg44GT44KT44Gr44Gh44Gv"; // "Hello, 世界! こんにちは"
        String decoded = mapper.decode(encoded);
        assertEquals("Hello, 世界! こんにちは", decoded);
    }

    @Test
    void test_decode_utf8Characters() {
        String encoded = "8J+YgCBIZWxsbyDwn5iA"; // "😀 Hello 😀"
        String decoded = mapper.decode(encoded);
        assertEquals("😀 Hello 😀", decoded);
    }

    @Test
    void test_decode_numbersAndSymbols() {
        String encoded = "MTIzNDU2Nzg5MA=="; // "1234567890"
        String decoded = mapper.decode(encoded);
        assertEquals("1234567890", decoded);
    }

    @Test
    void test_decode_nullSafety() {
        // Testing with a string that will cause NullPointerException if not handled
        String malformed = "a"; // Too short to be valid base64
        String decoded = mapper.decode(malformed);
        assertEquals(malformed, decoded); // Should return original on error
    }
}