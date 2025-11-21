package com.healthtrack.controller;

import com.healthtrack.entity.FamilyGroup;
import com.healthtrack.entity.FamilyGroupMember;
import com.healthtrack.service.FamilyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family-groups")
@RequiredArgsConstructor
public class FamilyGroupController {

    private final FamilyGroupService familyGroupService;

    // -------------------------
    // FamilyGroup 主模块
    // -------------------------
    @PostMapping
    public FamilyGroup createGroup(@RequestBody FamilyGroup group) {
        return familyGroupService.createGroup(group);
    }

    @GetMapping("/{groupId}")
    public FamilyGroup getGroup(@PathVariable Long groupId) {
        return familyGroupService.getGroup(groupId);
    }

    @GetMapping
    public List<FamilyGroup> getAllGroups() {
        return familyGroupService.getAllGroups();
    }

    @PutMapping
    public FamilyGroup updateGroup(@RequestBody FamilyGroup group) {
        return familyGroupService.updateGroup(group);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        familyGroupService.deleteGroup(groupId);
    }

    // -------------------------
    // 成员管理
    // -------------------------
    @GetMapping("/{groupId}/members")
    public List<FamilyGroupMember> getMembers(@PathVariable Long groupId) {
        return familyGroupService.getMembers(groupId);
    }

    @PostMapping("/{groupId}/members/{userId}")
    public FamilyGroupMember addMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam String role) {

        return familyGroupService.addMember(groupId, userId, role);
    }

    @DeleteMapping("/members/{memberId}")
    public void removeMember(@PathVariable Long memberId) {
        familyGroupService.removeMember(memberId);
    }
}
