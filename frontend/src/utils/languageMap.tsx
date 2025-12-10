// language mapping
import { LanguageVersion } from "../enums/LanguageVersion";
const monacoLanguageMap: Record<LanguageVersion, string> = {
  PYTHON_3_8: "python",
  PYTHON_2_7: "python",

  C_GCC_7_4: "c",
  C_GCC_8_3: "c",
  C_GCC_9_2: "c",
  C_CLANG_7_0: "c",

  CPP_GCC_7_4: "cpp",
  CPP_GCC_8_3: "cpp",
  CPP_GCC_9_2: "cpp",
  CPP_CLANG_7_0: "cpp",

  JAVA_OPENJDK_11: "java",
  JAVA_OPENJDK_13: "java",

  JAVASCRIPT_NODEJS_12: "javascript",

  CSHARP_MONO: "csharp",

  GO: "go",
  KOTLIN: "kotlin",
  RUST: "rust",
  RUBY: "ruby",
  PHP: "php",
  LUA: "lua",
  SWIFT: "swift",
  R: "r",

  BASH: "shell",
  BASIC: "basic",
  CLOJURE: "clojure",
  COBOL: "cobol",
  COMMON_LISP: "lisp",
  D: "d",
  ELIXIR: "elixir",
  ERLANG: "erlang",
  FORTRAN: "fortran",
  GROOVY: "groovy",
  HASKELL: "haskell",
  OCAML: "ocaml",
  OCTAVE: "octave",
  PASCAL: "pascal",
  PERL: "perl",
  PROLOG: "prolog",
  OBJECTIVEC: "objective-c",
  FSHARP: "fsharp",
  SQL: "sql",
  VISUAL_BASIC: "vb",
  MULTIFILE_PROGRAM: "plaintext",
  EXECUTABLE: "plaintext",
};
export { monacoLanguageMap };
