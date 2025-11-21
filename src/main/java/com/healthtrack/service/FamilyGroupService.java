package com.healthtrack.service;

import com.healthtrack.entity.FamilyGroup;
import com.healthtrack.entity.FamilyGroupMember;
import com.healthtrack.entity.User;
import com.healthtrack.repository.FamilyGroupMemberRepository;
import com.healthtrack.repository.FamilyGroupRepository;
import com.healthtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyGroupService {

    private final FamilyGroupRepository familyGroupRepository;
    private final FamilyGroupMemberRepository memberRepository;
    private final UserRepository userRepository;

    // -------------------------------------------------
    // FamilyGroup CRUD
    // -------------------------------------------------
    public FamilyGroup createGroup(FamilyGroup group) {
        return familyGroupRepository.save(group);
    }

    public FamilyGroup getGroup(Long groupId) {
        return familyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Family group not found"));
    }

    public List<FamilyGroup> getAllGroups() {
        return familyGroupRepository.findAll();
    }

    public FamilyGroup updateGroup(FamilyGroup group) {
        return familyGroupRepository.save(group);
    }

    public void deleteGroup(Long groupId) {
        familyGroupRepository.deleteById(groupId);
    }

    // -------------------------------------------------
    // 成员管理（FamilyGroupMember）
    // -------------------------------------------------
    public List<FamilyGroupMember> getMembers(Long groupId) {
        FamilyGroup group = getGroup(groupId);
        return memberRepository.findByFamilyGroup(group);
    }

    public FamilyGroupMember addMember(Long groupId, Long userId, String role) {
        FamilyGroup group = getGroup(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FamilyGroupMember member = new FamilyGroupMember();
        member.setFamilyGroup(group);
        member.setUser(user);
        member.setRole(role);

        return memberRepository.save(member);
    }

    public void removeMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
