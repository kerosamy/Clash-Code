package com.clashcode.backend.repository;

import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.MatchParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, MatchParticipantId> {
}
