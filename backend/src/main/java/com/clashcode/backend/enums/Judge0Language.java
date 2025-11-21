package com.clashcode.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Judge0Language {
    ASSEMBLY_NASM_2_14_02(45, "Assembly (NASM 2.14.02)"),
    BASH_5_0_0(46, "Bash (5.0.0)"),
    BASIC_FBC_1_07_1(47, "Basic (FBC 1.07.1)"),
    C_CLANG_7_0_1(75, "C (Clang 7.0.1)"),
    CPP_CLANG_7_0_1(76, "C++ (Clang 7.0.1)"),
    C_GCC_7_4_0(48, "C (GCC 7.4.0)"),
    CPP_GCC_7_4_0(52, "C++ (GCC 7.4.0)"),
    C_GCC_8_3_0(49, "C (GCC 8.3.0)"),
    CPP_GCC_8_3_0(53, "C++ (GCC 8.3.0)"),
    C_GCC_9_2_0(50, "C (GCC 9.2.0)"),
    CPP_GCC_9_2_0(54, "C++ (GCC 9.2.0)"),
    CLOJURE_1_10_1(86, "Clojure (1.10.1)"),
    CSHARP_MONO_6_6_0(51, "C# (Mono 6.6.0.161)"),
    COBOL_GNUCOBOL_2_2(77, "COBOL (GnuCOBOL 2.2)"),
    COMMON_LISP_SBCL_2_0_0(55, "Common Lisp (SBCL 2.0.0)"),
    D_DMD_2_089_1(56, "D (DMD 2.089.1)"),
    ELIXIR_1_9_4(57, "Elixir (1.9.4)"),
    ERLANG_22_2(58, "Erlang (OTP 22.2)"),
    EXECUTABLE(44, "Executable"),
    FSHARP_DOTNET_3_1_202(87, "F# (.NET Core SDK 3.1.202)"),
    FORTRAN_GFORTRAN_9_2_0(59, "Fortran (GFortran 9.2.0)"),
    GO_1_13_5(60, "Go (1.13.5)"),
    GROOVY_3_0_3(88, "Groovy (3.0.3)"),
    HASKELL_GHC_8_8_1(61, "Haskell (GHC 8.8.1)"),
    JAVA_OPENJDK_13_0_1(62, "Java (OpenJDK 13.0.1)"),
    JAVASCRIPT_NODEJS_12_14_0(63, "JavaScript (Node.js 12.14.0)"),
    KOTLIN_1_3_70(78, "Kotlin (1.3.70)"),
    LUA_5_3_5(64, "Lua (5.3.5)"),
    MULTIFILE_PROGRAM(89, "Multi-file program"),
    OBJECTIVEC_CLANG_7_0_1(79, "Objective-C (Clang 7.0.1)"),
    OCAML_4_09_0(65, "OCaml (4.09.0)"),
    OCTAVE_5_1_0(66, "Octave (5.1.0)"),
    PASCAL_FPC_3_0_4(67, "Pascal (FPC 3.0.4)"),
    PERL_5_28_1(85, "Perl (5.28.1)"),
    PHP_7_4_1(68, "PHP (7.4.1)"),
    PLAIN_TEXT(43, "Plain Text"),
    PROLOG_GNU_1_4_5(69, "Prolog (GNU Prolog 1.4.5)"),
    PYTHON_2_7_17(70, "Python (2.7.17)"),
    PYTHON_3_8_1(71, "Python (3.8.1)"),
    R_4_0_0(80, "R (4.0.0)"),
    RUBY_2_7_0(72, "Ruby (2.7.0)"),
    RUST_1_40_0(73, "Rust (1.40.0)"),
    SCALA_2_13_2(81, "Scala (2.13.2)"),
    SQL_SQLITE_3_27_2(82, "SQL (SQLite 3.27.2)"),
    SWIFT_5_2_3(83, "Swift (5.2.3)"),
    TYPESCRIPT_3_7_4(74, "TypeScript (3.7.4)"),
    VISUAL_BASIC_NET(84, "Visual Basic.Net (vbnc 0.0.0.5943)");

    private final int id;
    private final String languageName;

    public static Judge0Language fromLabel(String label) {
        for (Judge0Language lang : Judge0Language.values()) {
            if (lang.languageName.equals(label)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Invalid Judge0 language name: " + label);
    }
}
