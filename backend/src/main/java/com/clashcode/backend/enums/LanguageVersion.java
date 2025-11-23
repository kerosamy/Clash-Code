package com.clashcode.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LanguageVersion {

    // Python
    PYTHON_3_8("Python", "3.8.1"),
    PYTHON_2_7("Python", "2.7.17"),

    // C
    C_GCC_7_4("C", "GCC 7.4.0"),
    C_GCC_8_3("C", "GCC 8.3.0"),
    C_GCC_9_2("C", "GCC 9.2.0"),
    C_CLANG_7_0("C", "Clang 7.0.1"),

    // C++
    CPP_GCC_7_4("C++", "GCC 7.4.0"),
    CPP_GCC_8_3("C++", "GCC 8.3.0"),
    CPP_GCC_9_2("C++", "GCC 9.2.0"),
    CPP_CLANG_7_0("C++", "Clang 7.0.1"),

    // Java
    JAVA_OPENJDK_13("Java", "OpenJDK 13"),
    JAVA_OPENJDK_11("Java", "OpenJDK 11"),

    // JavaScript / Node
    JAVASCRIPT_NODEJS_12("JavaScript", "Node.js 12.14.0"),

    // C#
    CSHARP_MONO("C#", "Mono 6.6.0.161"),

    // Go
    GO("Go", "1.13.5"),

    // Kotlin
    KOTLIN("Kotlin", "1.3.70"),

    // Rust
    RUST("Rust", "1.40.0"),

    // Ruby
    RUBY("Ruby", "2.7.0"),

    // PHP
    PHP("PHP", "7.4.1"),

    // Lua
    LUA("Lua", "5.3.5"),

    // Swift
    SWIFT("Swift", "5.2.3"),

    // R
    R("R", "4.0.0"),

    // Others (add as needed)
    BASH("Bash", "5.0.0"),
    BASIC("Basic", "FBC 1.07.1"),
    CLOJURE("Clojure", "1.10.1"),
    COBOL("COBOL", "GnuCOBOL 2.2"),
    COMMON_LISP("Common Lisp", "SBCL 2.0.0"),
    D("D", "DMD 2.089.1"),
    ELIXIR("Elixir", "1.9.4"),
    ERLANG("Erlang", "OTP 22.2"),
    FORTRAN("Fortran", "GFortran 9.2.0"),
    GROOVY("Groovy", "3.0.3"),
    HASKELL("Haskell", "GHC 8.8.1"),
    OCAML("OCaml", "4.09.0"),
    OCTAVE("Octave", "5.1.0"),
    PASCAL("Pascal", "FPC 3.0.4"),
    PERL("Perl", "5.28.1"),
    PROLOG("Prolog", "GNU Prolog 1.4.5"),
    OBJECTIVEC("Objective-C", "Clang 7.0.1"),
    FSHARP(".NET F#", "3.1.202"),
    SQL("SQL", "SQLite 3.27.2"),
    VISUAL_BASIC("Visual Basic.Net", "vbnc 0.0.0.5943"),
    MULTIFILE_PROGRAM("Multi-file", "Program"),
    EXECUTABLE("Executable", "");

    private final String language;
    private final String version;


}
