package com.healthtrack.service;

import com.healthtrack.entity.Challenge;
import com.healthtrack.entity.ChallengeParticipant;
import com.healthtrack.entity.User;
import com.healthtrack.repository.ChallengeParticipantRepository;
import com.healthtrack.repository.ChallengeRepository;
import com.healthtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final UserRepository userRepository;

    // 新增 Challenge
    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    // 获取所有 Challenge
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    // 根据 ID 获取 Challenge
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    // ============================
    // ChallengeParticipant 相关操作
    // ============================

    @Transactional
    public ChallengeParticipant addParticipant(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChallengeParticipant participant = new ChallengeParticipant();
        participant.setChallenge(challenge);
        participant.setUser(user);
        participant.setStatus(ChallengeParticipant.ParticipantStatus.JOINED);

        return participantRepository.save(participant);
    }

    public List<ChallengeParticipant> getParticipants(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        return participantRepository.findByChallenge(challenge);
    }

    @Transactional
    public void removeParticipant(Long challengeId, Long userId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChallengeParticipant participant = participantRepository.findByChallengeAndUser(challenge, user)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participantRepository.delete(participant);
    }

    @Transactional
    public ChallengeParticipant updateParticipantStatus(Long challengeId, Long userId, ChallengeParticipant.ParticipantStatus status) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChallengeParticipant participant = participantRepository.findByChallengeAndUser(challenge, user)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        participant.setStatus(status);
        return participantRepository.save(participant);
    }
}
