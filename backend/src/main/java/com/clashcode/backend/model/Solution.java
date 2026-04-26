package com.clashcode.backend.model;

import com.clashcode.backend.enums.LanguageVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solution {

    @Column(name = "solution_code" , nullable = false , columnDefinition = "TEXT")
    private String solutionCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageVersion languageVersion;

}
