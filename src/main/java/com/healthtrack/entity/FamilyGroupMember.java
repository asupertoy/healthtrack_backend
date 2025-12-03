package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "family_group_members",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id", "user_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore // 数据库内部标识，不对前端暴露
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private FamilyGroup familyGroup;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "role", length = 50)
    private String role = "member";

    @Column(name = "permission", length = 100)
    private String permission;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;

    // 对前端暴露 userId，方便关联用户
    public Long getUserId() {
        return user != null ? user.getUserId() : null;
    }

    // 对前端暴露 userName，方便直接展示成员名字
    public String getUserName() {
        return user != null ? user.getName() : null;
    }
}
