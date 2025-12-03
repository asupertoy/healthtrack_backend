package com.healthtrack.service;

import com.healthtrack.dto.ChallengeStats;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        // 默认状态在实体中为 invited，如需立即标记为 joined 可以使用小写 joined 枚举
        // participant.setStatus(ChallengeParticipant.ParticipantStatus.joined);

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

    /**
     * 返回用户参与的所有 Challenge（去重）
     */
    public List<Challenge> getChallengesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChallengeParticipant> parts = participantRepository.findByUser(user);

        return parts.stream()
                .map(ChallengeParticipant::getChallenge)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 返回所有挑战的参与人数与完成人数统计
     */
    public List<ChallengeStats> getAllChallengeStats() {
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.stream().map(ch -> {
            long total = participantRepository.countByChallenge(ch);
            long completed = participantRepository.countByChallengeAndStatus(ch, ChallengeParticipant.ParticipantStatus.completed);
            return new ChallengeStats(ch.getChallengeId(), ch.getTitle(), total, completed);
        }).collect(Collectors.toList());
    }

    /**
     * 返回某个用户尚未参加的所有 Challenge
     */
    public List<Challenge> getChallengesNotJoinedByUser(Long userId) {
        // 已参加的挑战
        List<Challenge> joined = getChallengesByUser(userId);
        if (joined.isEmpty()) {
            // 用户尚未参与任何挑战，则未参与列表就是全部挑战
            return challengeRepository.findAll();
        }
        List<Long> joinedIds = joined.stream()
                .map(Challenge::getChallengeId)
                .toList();

        return challengeRepository.findByChallengeIdNotIn(joinedIds);
    }
}
