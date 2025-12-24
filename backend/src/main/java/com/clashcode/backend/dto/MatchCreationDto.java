package com.clashcode.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatchCreationDto {
   private Long playerIdA;
   private Long playerIdB;
}
