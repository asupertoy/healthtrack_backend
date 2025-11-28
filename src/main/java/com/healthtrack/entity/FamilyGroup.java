package com.healthtrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "family_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", length = 50)
    private String groupName;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    /* --------------------- Relations ---------------------- */
    @OneToMany(mappedBy = "familyGroup", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FamilyGroupMember> members;
}
