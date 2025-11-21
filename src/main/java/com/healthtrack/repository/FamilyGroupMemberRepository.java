package com.healthtrack.repository;

import com.healthtrack.entity.FamilyGroup;
import com.healthtrack.entity.FamilyGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyGroupMemberRepository extends JpaRepository<FamilyGroupMember, Long> {

    List<FamilyGroupMember> findByFamilyGroup(FamilyGroup familyGroup);
}
