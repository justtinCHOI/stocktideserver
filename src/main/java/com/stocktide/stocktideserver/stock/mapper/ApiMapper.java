package com.stocktide.stocktideserver.stock.mapper;

import com.stocktide.stocktideserver.stock.dto.StockMinDto;
import com.stocktide.stocktideserver.stock.dto.StockasbiDataDto;
import com.stocktide.stocktideserver.stock.entity.StockAsBi;
import com.stocktide.stocktideserver.stock.entity.StockInf;
import com.stocktide.stocktideserver.stock.entity.StockMin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApiMapper {
    @Mapping(target = "stockAsBiId", ignore = true) // 데이터베이스에서 생성되므로 무시
    @Mapping(target = "company", ignore = true) //  필요한 경우 적절히 처리
    StockAsBi stockAsBiOutput1ToStockAsBi(StockasbiDataDto.StockAsBiOutput1 stock);
    @Mapping(target = "stockInfId", ignore = true) // 데이터베이스에서 생성되므로 무시
    @Mapping(target = "company", ignore = true) // 필요시 적절히 처리
    StockInf stockMinOutput1ToStockInf(StockMinDto.StockMinOutput1 stock);
    @Mapping(target = "stockMinId", ignore = true) // 데이터베이스에서 생성되므로 무시
    @Mapping(target = "company", ignore = true) // 필요시 적절히 처리
    @Mapping(target = "stockTradeTime", ignore = true) // 필요시 적절히 처리
    StockMin stockMinOutput2ToStockMin(StockMinDto.StockMinOutput2 stock);
}
