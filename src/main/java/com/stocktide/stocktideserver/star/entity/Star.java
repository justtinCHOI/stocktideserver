package com.stocktide.stocktideserver.star.entity;

import com.stocktide.stocktideserver.audit.Auditable;
import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.stock.entity.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Star extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long starId;
    //member 또는 company 접근하는 시점에 로딩
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
}
