package com.stocktide.stocktideserver.stock.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockInfResponseDto {
    private long stockInfId;

    private long companyId;

    //주식 현재가
    private String stck_prpr;
    //전일 대비
    private String prdy_vrss;
    //전일 대비율
    private String prdy_ctrt;
    //누적 거래량
    private String acml_vol;
    //누적 거래대금
    private String acml_tr_pbmn;
}
