package com.clashcode.backend.judge.Judge0;

import com.clashcode.backend.dto.ExecutionResultDto;
import com.clashcode.backend.enums.LanguageVersion;

public class Judge0Mapper {

    public int mapToJudge0Id(LanguageVersion lv) {
        return switch (lv) {
            // Python
            case PYTHON_3_8 -> 71;
            case PYTHON_2_7 -> 70;

            // C
            case C_GCC_7_4 -> 48;
            case C_GCC_8_3 -> 49;
            case C_GCC_9_2 -> 50;
            case C_CLANG_7_0 -> 75;

            // C++
            case CPP_GCC_7_4 -> 52;
            case CPP_GCC_8_3 -> 53;
            case CPP_GCC_9_2 -> 54;
            case CPP_CLANG_7_0 -> 76;

            // Java
            case JAVA_OPENJDK_13 -> 62;
            case JAVA_OPENJDK_11 -> 63;

            // JavaScript / Node
            case JAVASCRIPT_NODEJS_12 -> 63;

            // C#
            case CSHARP_MONO -> 51;

            // Go
            case GO -> 60;

            // Kotlin
            case KOTLIN -> 78;

            // Rust
            case RUST -> 73;

            // Ruby
            case RUBY -> 72;

            // PHP
            case PHP -> 68;

            // Lua
            case LUA -> 64;

            // Swift
            case SWIFT -> 83;

            // R
            case R -> 80;

            // Others
            case BASH -> 46;
            case BASIC -> 47;
            case CLOJURE -> 86;
            case COBOL -> 77;
            case COMMON_LISP -> 55;
            case D -> 56;
            case ELIXIR -> 57;
            case ERLANG -> 58;
            case FORTRAN -> 59;
            case GROOVY -> 88;
            case HASKELL -> 61;
            case OCAML -> 65;
            case OCTAVE -> 66;
            case PASCAL -> 67;
            case PERL -> 85;
            case PROLOG -> 69;
            case OBJECTIVEC -> 79;
            case FSHARP -> 87;
            case SQL -> 82;
            case VISUAL_BASIC -> 84;
            case MULTIFILE_PROGRAM -> 89;
            case EXECUTABLE -> 44;
        };
    }

    public ExecutionResultDto toExecutionResultDto (Judge0ResponseDto judge0ResponseDto){
        ExecutionResultDto result = ExecutionResultDto.builder()
                                    .timeTaken((int)(judge0ResponseDto.getTime()*100))
                                    .memoryTaken((int)(judge0ResponseDto.getMemory()*100))
                                    .status(judge0ResponseDto.getStatus().getDescription())
                                    .result(judge0ResponseDto.getStdout())
                                    .build();

        return result;
    }
}


