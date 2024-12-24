package com.stocktide.stocktideserver.stock.service;

import com.stocktide.stocktideserver.exception.BusinessLogicException;
import com.stocktide.stocktideserver.exception.ExceptionCode;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import com.stocktide.stocktideserver.stock.dto.StockHoldResponseDto;
import com.stocktide.stocktideserver.stock.entity.Company;
import com.stocktide.stocktideserver.stock.entity.StockHold;
import com.stocktide.stocktideserver.stock.mapper.StockMapper;
import com.stocktide.stocktideserver.stock.repository.CompanyRepository;
import com.stocktide.stocktideserver.stock.repository.StockHoldRepository;
import com.stocktide.stocktideserver.stock.repository.StockOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StockHoldService {
    private final StockHoldRepository stockHoldRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final StockOrderRepository stockOrderRepository;
    private final StockMapper stockMapper;

    //memberId + companyId -> StockHold 없으면 생성
    public StockHold checkStockHold(long companyId, long memberId) {
        StockHold stockHold = stockHoldRepository.findByCompanyCompanyIdAndMemberMemberId(companyId, memberId);
        if(stockHold == null) {
            StockHold newStockHold = new StockHold();
            newStockHold.setMember(memberRepository.findById(memberId).get());
            newStockHold.setCompany(companyRepository.findById(companyId).get());
            return newStockHold;
        }
        else
            return stockHold;
    }

    //memberId + companyId -> StockHold 없으면 오류반환
    public StockHold findStockHold(long companyId, long memberId) {
        StockHold stockHold = stockHoldRepository.findByCompanyCompanyIdAndMemberMemberId(companyId, memberId);
        if(stockHold == null)
            throw new BusinessLogicException(ExceptionCode.STOCKHOLD_NOT_FOUND);
        else
            return stockHold;
    }

     //회원의 모든 StockHold
     public List<StockHold> getMemberStockHolds(long memberId) {
        List<StockHold> stockHolds = stockHoldRepository.findAllByMember_MemberId(memberId);


        return stockHolds;
    }

    //회원의 모든 StockHoldResponseDto
    // List<StockHold> ->  List<StockHoldResponseDto>
    public List<StockHoldResponseDto> findStockHolds(Long memberId, Long companyId) {
        List<StockHold> stockHoldList = stockHoldRepository.findAllByMember_MemberId(memberId);
        if(stockHoldList.isEmpty()) {
            StockHold newStockHold = new StockHold();
            newStockHold.setMember(memberRepository.findById(memberId).get());
            newStockHold.setCompany(companyRepository.findById(companyId).get());
            stockHoldList.add(newStockHold);
        }
        List<StockHoldResponseDto> stockHoldResponseDtos = stockMapper.stockHoldToStockHoldResponseDto(stockHoldList);
        // 특정 조건의 예약된 주식 수량을 설정하는 로직
//        for(StockHoldResponseDto stockHold : stockHoldResponseDtos) {
//
//            List<StockOrder> stockOrders =  stockOrderRepository
//                    .findAllByMember_MemberIdAndCompany_CompanyIdAndOrderStatesAndOrderTypes(
//                            stockHold.getMemberId(),
//                            stockHold.getCompanyId(),
//                            StockOrder.OrderStates.ORDER_WAIT,
//                            StockOrder.OrderTypes.SELL
//                    );
//            int orderWaitCount = stockOrders.stream().mapToInt(StockOrder::getStockCount).sum();
//            stockHold.setReserveSellStockCount(orderWaitCount);
//        }

        return stockHoldResponseDtos;
    }

    //  List<StockHoldResponseDto> ->  수익금, 수익률 계산 update
    // stockHoldResponseDto -> company -> nowPrice -> totalRevenue -> percentage -> stockHoldResponseDto
    public List<StockHoldResponseDto> setPercentage(List<StockHoldResponseDto> stockHoldResponseDtos) {
        for(StockHoldResponseDto stockHoldResponseDto : stockHoldResponseDtos) {
            // 이름으로 회사를 불러온다
            Company company = companyRepository.findByCompanyId(stockHoldResponseDto.getCompanyId());
            // 주식 현재가를 불러온다
            String nowPrice = company.getStockInf().getStck_prpr();
            // 주식 수익 = 전체 주식 가치 - 전체 투자 금액
            double totalRevenue =
                    Double.parseDouble(nowPrice)
                            * (stockHoldResponseDto.getStockCount()+stockHoldResponseDto.getReserveSellStockCount())
                            - stockHoldResponseDto.getTotalPrice();
            // 주식 수익률(%) = (주식 수익 / 전체 투자 금액) × 100
            double percentage = (totalRevenue / (double)stockHoldResponseDto.getTotalPrice()) * 100;

            stockHoldResponseDto.setPercentage(percentage);
            stockHoldResponseDto.setStockReturn((long) totalRevenue);
        }
        return stockHoldResponseDtos;
    }

    //보유 주식 전부 삭제
    public void deleteStockHolds(long memberId) {
        List<StockHold> stockHolds = getMemberStockHolds(memberId);
        stockHoldRepository.deleteAll(stockHolds);
    }
}
