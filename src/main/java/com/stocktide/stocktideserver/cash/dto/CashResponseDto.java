package com.stocktide.stocktideserver.cash.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CashResponseDto {
    private long cashId;

    private String accountNumber;

    private long money;

    private long dollar;

}
