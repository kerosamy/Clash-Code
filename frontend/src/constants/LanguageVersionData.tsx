import { LanguageVersion } from "../enums/ProgrammingLanguage";

export interface LanguageVersionInfo {
  key: LanguageVersion;
  language: string;
  version: string;
}

export const LanguageVersionData: Record<LanguageVersion, { language: string; version: string }> = {
  [LanguageVersion.PYTHON_3_8]: { language: "Python", version: "3.8.1" },
  [LanguageVersion.PYTHON_2_7]: { language: "Python", version: "2.7.17" },

  [LanguageVersion.C_GCC_7_4]: { language: "C", version: "GCC 7.4.0" },
  [LanguageVersion.C_GCC_8_3]: { language: "C", version: "GCC 8.3.0" },
  [LanguageVersion.C_GCC_9_2]: { language: "C", version: "GCC 9.2.0" },
  [LanguageVersion.C_CLANG_7_0]: { language: "C", version: "Clang 7.0.1" },

  [LanguageVersion.CPP_GCC_7_4]: { language: "C++", version: "GCC 7.4.0" },
  [LanguageVersion.CPP_GCC_8_3]: { language: "C++", version: "GCC 8.3.0" },
  [LanguageVersion.CPP_GCC_9_2]: { language: "C++", version: "GCC 9.2.0" },
  [LanguageVersion.CPP_CLANG_7_0]: { language: "C++", version: "Clang 7.0.1" },

  [LanguageVersion.JAVA_OPENJDK_13]: { language: "Java", version: "OpenJDK 13" },
  [LanguageVersion.JAVA_OPENJDK_11]: { language: "Java", version: "OpenJDK 11" },

  [LanguageVersion.JAVASCRIPT_NODEJS_12]: { language: "JavaScript", version: "Node.js 12.14.0" },

  [LanguageVersion.CSHARP_MONO]: { language: "C#", version: "Mono 6.6.0.161" },

  [LanguageVersion.GO]: { language: "Go", version: "1.13.5" },
  [LanguageVersion.KOTLIN]: { language: "Kotlin", version: "1.3.70" },
  [LanguageVersion.RUST]: { language: "Rust", version: "1.40.0" },
  [LanguageVersion.RUBY]: { language: "Ruby", version: "2.7.0" },
  [LanguageVersion.PHP]: { language: "PHP", version: "7.4.1" },
  [LanguageVersion.LUA]: { language: "Lua", version: "5.3.5" },
  [LanguageVersion.SWIFT]: { language: "Swift", version: "5.2.3" },
  [LanguageVersion.R]: { language: "R", version: "4.0.0" },

  [LanguageVersion.BASH]: { language: "Bash", version: "5.0.0" },
  [LanguageVersion.BASIC]: { language: "Basic", version: "FBC 1.07.1" },
  [LanguageVersion.CLOJURE]: { language: "Clojure", version: "1.10.1" },
  [LanguageVersion.COBOL]: { language: "COBOL", version: "GnuCOBOL 2.2" },
  [LanguageVersion.COMMON_LISP]: { language: "Common Lisp", version: "SBCL 2.0.0" },
  [LanguageVersion.D]: { language: "D", version: "DMD 2.089.1" },
  [LanguageVersion.ELIXIR]: { language: "Elixir", version: "1.9.4" },
  [LanguageVersion.ERLANG]: { language: "Erlang", version: "OTP 22.2" },
  [LanguageVersion.FORTRAN]: { language: "Fortran", version: "GFortran 9.2.0" },
  [LanguageVersion.GROOVY]: { language: "Groovy", version: "3.0.3" },
  [LanguageVersion.HASKELL]: { language: "Haskell", version: "GHC 8.8.1" },
  [LanguageVersion.OCAML]: { language: "OCaml", version: "4.09.0" },
  [LanguageVersion.OCTAVE]: { language: "Octave", version: "5.1.0" },
  [LanguageVersion.PASCAL]: { language: "Pascal", version: "FPC 3.0.4" },
  [LanguageVersion.PERL]: { language: "Perl", version: "5.28.1" },
  [LanguageVersion.PROLOG]: { language: "Prolog", version: "GNU Prolog 1.4.5" },
  [LanguageVersion.OBJECTIVEC]: { language: "Objective-C", version: "Clang 7.0.1" },
  [LanguageVersion.FSHARP]: { language: ".NET F#", version: "3.1.202" },
  [LanguageVersion.SQL]: { language: "SQL", version: "SQLite 3.27.2" },
  [LanguageVersion.VISUAL_BASIC]: { language: "Visual Basic.Net", version: "vbnc 0.0.0.5943" },

  [LanguageVersion.MULTIFILE_PROGRAM]: { language: "Multi-file", version: "Program" },
  [LanguageVersion.EXECUTABLE]: { language: "Executable", version: "" },
};

export function getAllLanguageVersions(): LanguageVersionInfo[] {
  return Object.values(LanguageVersion).map((key) => ({
    key,
    language: LanguageVersionData[key].language,
    version: LanguageVersionData[key].version,
  }));
}
