package com.clashcode.backend.controller;

import com.clashcode.backend.dto.CreateMatchRequestDto;
import com.clashcode.backend.dto.MatchResponseDto;
import com.clashcode.backend.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/create")
    public ResponseEntity<MatchResponseDto> createMatch(@RequestBody CreateMatchRequestDto createMatchRequestDto) {
        MatchResponseDto matchResponseDto = matchService.createMatch(createMatchRequestDto);
        return ResponseEntity.ok(matchResponseDto);
    }

}
