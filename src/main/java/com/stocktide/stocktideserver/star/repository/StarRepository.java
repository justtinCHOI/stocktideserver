package com.stocktide.stocktideserver.star.repository;

import com.stocktide.stocktideserver.star.entity.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StarRepository extends JpaRepository<Star, Long> {
    @Query("SELECT s FROM Star s JOIN FETCH s.member JOIN FETCH s.company WHERE s.member.memberId = :memberId")
    List<Star> findAllByMember_MemberId(long memberId);

    Star findByMember_MemberIdAndCompanyCompanyId(long memberId, long companyId);
}
