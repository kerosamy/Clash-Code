package com.clashcode.backend.dto;

public class VerifyRecoveryDto {
    private String email;
    private String answer;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}