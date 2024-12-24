package com.stocktide.stocktideserver.stock.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockHoldResponseDto {
    private long stockHoldId;

    private long memberId; //from member

    private long companyId; //from company

    private String companyKorName; // from company

    private int stockCount; // from stockCount

    private long totalPrice; // from price

    private double percentage; // 0D  수익률

    private long stockReturn; // 0 주식 수익

    private long reserveSellStockCount; //from reserveStockCount
}
