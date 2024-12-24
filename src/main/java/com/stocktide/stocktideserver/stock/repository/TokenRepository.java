package com.stocktide.stocktideserver.stock.repository;

import com.stocktide.stocktideserver.stock.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
