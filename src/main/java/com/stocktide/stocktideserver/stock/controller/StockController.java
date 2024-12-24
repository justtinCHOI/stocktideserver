package com.stocktide.stocktideserver.stock.controller;


import com.stocktide.stocktideserver.stock.service.ApiCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final ApiCallService apiCallService;
    @GetMapping("/api/kospi")
    public ResponseEntity getKospiMonth() {
        String kospi = apiCallService.getKospiMonthFromApi();
        return ResponseEntity.ok(kospi);
    }

}

