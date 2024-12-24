package com.stocktide.stocktideserver.stock.repository;

import com.stocktide.stocktideserver.stock.entity.StockInf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockInfRepository extends JpaRepository<StockInf, Long> {
}
