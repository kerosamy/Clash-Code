package com.clashcode.backend.dto;

import com.clashcode.backend.enums.ProblemTags;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    String name;
    int value;
}
