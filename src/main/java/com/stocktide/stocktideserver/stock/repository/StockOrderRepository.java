package com.stocktide.stocktideserver.stock.repository;

import com.stocktide.stocktideserver.stock.entity.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {
    List<StockOrder> findAllByCompanyCompanyIdAndOrderStates(long company_companyId, StockOrder.OrderStates orderStates);
    List<StockOrder> findAllByMember_MemberId(long memberId);
    List<StockOrder> findAllByMember_MemberIdOrderByModifiedAtDesc(long memberId);
    List<StockOrder> findAllByMember_MemberIdAndCompany_CompanyIdAndOrderStatesAndOrderTypes(long memberId, long companyId, StockOrder.OrderStates orderStates, StockOrder.OrderTypes orderTypes);

    List<StockOrder> findByMemberMemberId(long memberId);
    // MEMBER_ID로 주식 주문을 모두 삭제하는 JPQL 쿼리
//    @Modifying
//    @Query("DELETE FROM StockOrder so WHERE so.memberId = :memberId")
//    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
