package com.stocktide.stocktideserver.stock.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockInf { // 회사 리스트를 위한 정보
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stockInfId;

    @OneToOne
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    // 주식 현재가
    @Column
    private String stck_prpr;

    // 전일 대비
    @Column
    private String prdy_vrss;

    // 전일 대비율
    @Column
    private String prdy_ctrt;

    // 누적 거래량
    @Column
    private String acml_vol;

    // 누적 거래대금
    @Column
    private String acml_tr_pbmn;
}
