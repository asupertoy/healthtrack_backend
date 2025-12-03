package com.healthtrack.repository;

import com.healthtrack.entity.Challenge;
import com.healthtrack.entity.ChallengeParticipant;
import com.healthtrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    List<ChallengeParticipant> findByChallenge(Challenge challenge);

    Optional<ChallengeParticipant> findByChallengeAndUser(Challenge challenge, User user);

    List<ChallengeParticipant> findByUser(User user);

    // count helpers
    long countByChallenge(Challenge challenge);

    long countByChallengeAndStatus(Challenge challenge, ChallengeParticipant.ParticipantStatus status);
}
