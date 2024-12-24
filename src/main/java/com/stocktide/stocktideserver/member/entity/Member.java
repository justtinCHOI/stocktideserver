package com.stocktide.stocktideserver.member.entity;

import com.stocktide.stocktideserver.audit.Auditable;
import com.stocktide.stocktideserver.cash.entity.Cash;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.stocktide.stocktideserver.member.entity.MemberStatus.ACTIVE;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 30, nullable = false)
    private String email;

    @Column(length = 10, nullable = false)
    private String name;

    private String nickname;

    @Column(length = 255, nullable = true)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,  orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Cash> cashList = new ArrayList<>();

    private boolean social;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private MemberStatus memberStatus = ACTIVE;

    public void addRole(MemberRole memberRole){//권한 생성
        memberRoleList.add(memberRole);
    }

    public void clearRole(){ // 사용자가 가지고 있는 권한들 전부 삭제
        memberRoleList.clear();
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", social=" + social +
                ", memberStatus=" + memberStatus +
                ", createdAt=" + getCreatedAt() +
                ", modifiedAt=" + getModifiedAt() +
                '}';
    }
}
