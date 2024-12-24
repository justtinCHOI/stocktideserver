package com.stocktide.stocktideserver.stock.dto;

import com.stocktide.stocktideserver.audit.Auditable;
import com.stocktide.stocktideserver.stock.entity.StockAsBi;
import com.stocktide.stocktideserver.stock.entity.StockInf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyModifyDTO extends Auditable {
    private long companyId;

    private String code;

    private String korName;

    private StockAsBi stockAsBi;

    private StockInf stockInf;
}
