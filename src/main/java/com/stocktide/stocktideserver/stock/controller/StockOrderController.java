package com.stocktide.stocktideserver.stock.controller;

import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import com.stocktide.stocktideserver.stock.dto.StockHoldResponseDto;
import com.stocktide.stocktideserver.stock.dto.StockOrderResponseDto;
import com.stocktide.stocktideserver.stock.entity.StockOrder;
import com.stocktide.stocktideserver.stock.mapper.StockMapper;
import com.stocktide.stocktideserver.stock.service.StockHoldService;
import com.stocktide.stocktideserver.stock.service.StockOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockOrderController {

    private final StockOrderService stockOrderService;
    private final StockMapper stockMapper;
    private final StockHoldService stockHoldService;
    private final MemberRepository memberRepository;

    // 보유 주식 정보들 반환하는 api
    @GetMapping("/stockholds/{memberId}")
    public ResponseEntity<List<StockHoldResponseDto>> getStockHolds(@PathVariable("memberId") Long memberId, @RequestParam Long companyId) {

        List<StockHoldResponseDto> stockHoldResponseDtos = stockHoldService.findStockHolds(memberId, companyId);

        stockHoldResponseDtos = stockHoldService.setPercentage(stockHoldResponseDtos);

        return new ResponseEntity<>(stockHoldResponseDtos, HttpStatus.OK);

    }

    // 매수 api
    @PostMapping("/buy")
    public ResponseEntity<Object> buyStocks(@RequestParam(name = "companyId") long companyId,
                                    @RequestParam(name = "price") long price,
                                    @RequestParam(name = "stockCount") int stockCount,
                                    @RequestParam(name = "memberId") Long memberId) {
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        StockOrder stockOrder = stockOrderService.buyStocks(member.get(), companyId, price, stockCount);
        StockOrderResponseDto stockOrderResponseDto = stockMapper.stockOrderToStockOrderResponseDto(stockOrder);

        return new ResponseEntity<>(stockOrderResponseDto, HttpStatus.CREATED);
    }

    // 매도 api
    @PostMapping("/sell")
    public ResponseEntity<Object> sellStocks(@RequestParam(name = "companyId") long companyId,
                                     @RequestParam(name = "price") long price,
                                     @RequestParam(name = "stockCount") int stockCount,
                                     @RequestParam(name = "memberId") Long memberId) {
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        StockOrder stockOrder = stockOrderService.sellStocks(member.get(), companyId, price, stockCount);
        StockOrderResponseDto stockOrderResponseDto = stockMapper.stockOrderToStockOrderResponseDto(stockOrder);

        return new ResponseEntity<>(stockOrderResponseDto, HttpStatus.CREATED);
    }

}