package com.stocktide.stocktideserver.stock.controller;

import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.stock.dto.StockOrderResponseDto;
import com.stocktide.stocktideserver.stock.entity.StockOrder;
import com.stocktide.stocktideserver.stock.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/long-polling")
@RequiredArgsConstructor
public class LongPollingController {

    private List<StockOrder> updateBuyStockOrders;
    private List<StockOrder> updateSellStockOrders;
    private final StockMapper stockMapper;

    @GetMapping("/listen")
    public ResponseEntity listenForUpdate(Member member) throws InterruptedException {
        updateBuyStockOrders = new ArrayList<>();
        updateSellStockOrders = new ArrayList<>();

        waitForStockOrdersToUpdate();

        List<StockOrder> memberBuyStockOrder = updateBuyStockOrders.stream()
                .filter(stockOrder -> stockOrder.getMember().getMemberId() == member.getMemberId())
                .collect(Collectors.toList());

        List<StockOrder> memberSellStockOrder = updateSellStockOrders.stream()
                .filter(stockOrder -> stockOrder.getMember().getMemberId() == member.getMemberId())
                .collect(Collectors.toList());

        List<StockOrderResponseDto> buyStockOrderResponseDtos = stockMapper.stockOrdersToStockOrderResponseDtos(memberBuyStockOrder);
        List<StockOrderResponseDto> sellStockOrderResponseDtos = stockMapper.stockOrdersToStockOrderResponseDtos(memberSellStockOrder);

        List<List<StockOrderResponseDto>> updateStockOrders = new ArrayList<>();
        updateStockOrders.add(buyStockOrderResponseDtos);
        updateStockOrders.add(sellStockOrderResponseDtos);

        return new ResponseEntity(updateStockOrders, HttpStatus.OK);
    }

    private void waitForStockOrdersToUpdate() throws InterruptedException {
        // 변경된 데이터가 도착할 때까지 대기
        synchronized (this) {
            if (updateBuyStockOrders.isEmpty() && updateSellStockOrders.isEmpty()) {
                wait();
            }
        }
    }

    public synchronized void notifyDataUpdated(List<StockOrder> buyStockOrders,
                                               List<StockOrder> sellStockOrders) {
        updateBuyStockOrders = buyStockOrders;
        updateSellStockOrders = sellStockOrders;
        notify(); // 대기 중인 스레드를 깨워 응답을 보냄
    }
}
