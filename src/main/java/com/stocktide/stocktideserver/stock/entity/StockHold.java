package com.stocktide.stocktideserver.stock.entity;

import com.stocktide.stocktideserver.audit.Auditable;
import com.stocktide.stocktideserver.member.entity.Member;
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
public class StockHold extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stockHoldId;

    // Member와 연관 StockHold 엔티티 자동 삭제
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    // 실제 보유 주식 수량
    @Column
    private int stockCount;

    // 특정 조건에서 예약한 주식 수량
    @Column
    private int reserveStockCount;

    // 총 투자 금액
    @Column
    private long price;

}
