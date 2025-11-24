// src/pages/SubmitSolution.tsx
import { useParams } from "react-router-dom";
import { useState } from "react";
import TestCases from "../components/TestCase";

export default function SubmitSolution() {
  const { id } = useParams(); // get problem ID from URL
  const [code, setCode] = useState("");
    const problem = {
    title: "A. Watermelon",
    timeLimit: "1 second",
    memoryLimit: "256 MB",
    statement:
      "You are given a watermelon. You need to check if you can split it into two parts such that each part weighs an even number of kilos. " +
      "You are given a watermelon. You need to check if you can split it into two parts such that each part weighs an even number of kilos. " +
      "You are given a watermelon. You need to check if you can split it into two parts such that each part weighs an even number of kilos.",
    inputFormat:
      "Single integer w (1 ≤ w ≤ 100) — the weight of the watermelon.",
    outputFormat:
      "Print 'YES' if the watermelon can be split. Otherwise, print 'NO'.",
    testcases: [
      { input: "8 \n1 2 3 4 5 6 7 8 ", output: "YES" },
      { input: "3", output: "NO" },
      { input: "10", output: "YES" },
    ],
    notes: "The weight must be divided into two positive integers.",
  };


  return (
   <p>submit</p>
     
  );
}
