package com.stocktide.stocktideserver.stock.repository;

import com.stocktide.stocktideserver.stock.entity.StockHold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHoldRepository extends JpaRepository<StockHold, Long> {
    StockHold findByCompanyCompanyIdAndMemberMemberId(long companyId, long memberId);
    List<StockHold> findAllByMember_MemberId(long memberId);
}
