package com.clashcode.matching_service.controller;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.clashcode.matching_service.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matching")
public class MatchingController {
    private final MatchingService matchingService;
    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/request-matching")
    ResponseEntity<Void> addUserToMatching(@RequestBody MatchingRequestDto matchRequestDto) {
        matchingService.addUserToMatchingService(matchRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-request/{userId}")
    ResponseEntity<Void> deleteUserFromMatching(@PathVariable Long userId) {
        matchingService.removeUserFromMatchingService(userId);
        return ResponseEntity.ok().build();
    }
}
