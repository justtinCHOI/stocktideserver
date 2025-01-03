package com.stocktide.stocktideserver.cash.dto;

//import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashPostDto {

//    @Schema(description = "금액", defaultValue = "5000000")
    @Min(value = 1000000, message = "금액은 최소 1,000,000 이상이어야 합니다.") // 최소값 설정
    @Max(value = 500000000, message = "금액은 최대 500,000,000 이하여야 합니다.") // 최대값 설정
    private long money;
}
