package com.clashcode.backend.dto;

import com.clashcode.backend.enums.ProblemTags;
import java.util.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemListDto {
    private Long id;
    private String title;
    private Long submissionsCount;
    private List<ProblemTags> tags;
    private int rate;

    //private string for attempted holder empty for now waiting Submission entity
    private String attempted = "unsolved";

}
