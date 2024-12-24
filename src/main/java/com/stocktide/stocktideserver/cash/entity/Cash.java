package com.stocktide.stocktideserver.cash.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.stocktide.stocktideserver.audit.Auditable;
import com.stocktide.stocktideserver.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cash extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cashId;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Builder.Default
    @Column(nullable = false)
    private long money = 0;

    @Builder.Default
    @Column(nullable = false)
    private long dollar = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    @JsonBackReference
    private Member member;

    @Override
    public String toString() {
        return "Cash{" +
                "cashId=" + cashId +
                ", accountNumber='" + accountNumber + '\'' +
                ", money=" + money +
                ", dollar=" + dollar +
                '}';
    }
}
