package com.stocktide.stocktideserver.cash.mapper;

import com.stocktide.stocktideserver.cash.dto.CashResponseDto;
import com.stocktide.stocktideserver.cash.entity.Cash;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashMapper {

//    Cash cashPostToCash(CashPostDto requestBody);
//
//    Cash cashPatchToCash(CashPatchDto requestBody);
    CashResponseDto cashToCashResponseDto(Cash cash);
}
