package com.stocktide.stocktideserver.cash.repository;

import com.stocktide.stocktideserver.cash.entity.Cash;
import com.stocktide.stocktideserver.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashRepository extends JpaRepository<Cash, Long> {

    List<Cash> findByMember(Member member);

    boolean existsByAccountNumber(String accountNumber);

    Cash findByCashId(Long cashId);
}
