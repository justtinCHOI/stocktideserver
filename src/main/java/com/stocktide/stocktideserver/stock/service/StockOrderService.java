package com.stocktide.stocktideserver.stock.service;

import com.stocktide.stocktideserver.cash.entity.Cash;
import com.stocktide.stocktideserver.cash.service.CashService;
import com.stocktide.stocktideserver.exception.BusinessLogicException;
import com.stocktide.stocktideserver.exception.ExceptionCode;
import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import com.stocktide.stocktideserver.stock.controller.LongPollingController;
import com.stocktide.stocktideserver.stock.entity.StockAsBi;
import com.stocktide.stocktideserver.stock.entity.StockHold;
import com.stocktide.stocktideserver.stock.entity.StockOrder;
import com.stocktide.stocktideserver.stock.mapper.StockMapper;
import com.stocktide.stocktideserver.stock.repository.StockHoldRepository;
import com.stocktide.stocktideserver.stock.repository.StockOrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

// sellStocks(보유량) ->           sellDiscrimination(호가량)          -> sellStock확정        || (reserveStock -> StockOrder)
// buyStocks(보유현금) ->           buyDiscrimination(호가량)          -> buyStock확정          || (reserveStock -> StockOrder)
// checkOrder(OrderTypes)  ->      reserveBuyDiscrimination(호가량)   -> reserveBuyStock확정
// checkOrder(OrderTypes)  ->      reserveSellDiscrimination(호가량)  -> reserveSellStock확정
// deleteStockOrder
// sendStockOrder
@Service
@Transactional
@Slf4j
public class StockOrderService {
    private final StockAsBiService stockAsBiService;
    private final CompanyService companyService;
    private final StockOrderRepository stockOrderRepository;
    private final MemberRepository memberRepository;
    private final StockHoldService stockHoldService;
    private final StockHoldRepository stockHoldRepository;
    private final CashService cashService;
    private final StockMapper stockMapper;
    private final LongPollingController longPollingController;
    private final SimpMessagingTemplate messagingTemplate;

    public StockOrderService(StockAsBiService stockAsBiService, CompanyService companyService, StockOrderRepository stockOrderRepository, MemberRepository memberRepository, StockHoldService stockHoldService, StockHoldRepository stockHoldRepository, CashService cashService, StockMapper stockMapper, LongPollingController longPollingController, SimpMessagingTemplate messagingTemplate) {
        this.stockAsBiService = stockAsBiService;
        this.companyService = companyService;
        this.stockOrderRepository = stockOrderRepository;
        this.memberRepository = memberRepository;
        this.stockHoldService = stockHoldService;
        this.stockHoldRepository = stockHoldRepository;
        this.cashService = cashService;
        this.stockMapper = stockMapper;
        this.longPollingController = longPollingController;
        this.messagingTemplate = messagingTemplate;
    }

    // 멤버, 회사 id, 가격, 수량 ->
    public StockOrder buyStocks(Member member, long companyId, long price, int stockCount) {
        //회원 캐쉬 잔량 비교
        cashService.checkCash(price * stockCount, member); // -> 부족할 시 예외 처리
        //호가 불러오기
        StockAsBi stockAsBi = stockAsBiService.getStockAsBi(companyId);
        // 예약 구매인지 바로 구매인지 판별
        return buyDiscrimination(member, price, stockAsBi, stockCount, companyId);
    }

    private StockOrder buyDiscrimination(Member member, long price, StockAsBi stockAsBi, int stockCount, long companyId) {
        // 매도 호가와 가격이 같고, 잔량이 남아 있을 경우
        if(Long.parseLong(stockAsBi.getAskp1()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn1()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp2()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn2()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp3()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn3()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp4()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn4()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp5()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn5()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp6()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn6()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp7()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn7()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp8()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn8()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp9()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn9()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else if(Long.parseLong(stockAsBi.getAskp10()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn10()) > stockCount)
            return buyStock(member, price, stockCount, companyId); // 구매 로직
        else
            return reserveStock(member, price, stockCount, companyId, StockOrder.OrderTypes.BUY); //예약 구매 로직
    }

    public StockOrder buyStock(Member member, long price, int stockCount, long companyId) {
        // StockHold 수정||생성 ,보유주식 추가, 총 투자 금액 추가
        StockHold stockHold = stockHoldService.checkStockHold(companyId, member.getMemberId());
        stockHold.setStockCount(stockHold.getStockCount() + stockCount);
        stockHold.setPrice(stockHold.getPrice() + (stockCount * price));

        // StockOrder 생성
        StockOrder stockOrder = new StockOrder();
        stockOrder.setOrderStates(StockOrder.OrderStates.ORDER_COMPLETE); // 채결 완료
        stockOrder.setOrderTypes(StockOrder.OrderTypes.BUY); // 매수
        stockOrder.setStockCount(stockCount);
        stockOrder.setPrice(price);
        stockOrder.setCompany(companyService.findCompanyById(companyId));

        // 현금량 감소 , 연관 Entity 수정 (member 수정-> stockOrder 수정)
        List<Cash> cashList = member.getCashList();
        Cash cash = cashList.get(0);
        cash.setMoney(cash.getMoney()-(price * stockCount));
        cashList.set(0, cash);
        member.setCashList(cashList);
        stockOrder.setMember(member);
        stockOrderRepository.save(stockOrder);
        memberRepository.save(member);
        stockHoldRepository.save(stockHold);

        return stockOrder;
    }

    // 멤버, 회사 id, 매도가격, 매도량
    public StockOrder sellStocks(Member member, long companyId, long price, int stockCount) {
        // StockHold ->  보유량, 매도량 비교
        StockHold stockHold = stockHoldService.findStockHold(companyId, member.getMemberId());
        if(stockHold.getStockCount() < stockCount) //예외방출
            throw new BusinessLogicException(ExceptionCode.INSUFFICIENT_STOCK);
        else {
            StockAsBi stockAsBi = stockAsBiService.getStockAsBi(companyId);
            //  호가량, 매도량 비교 ->  예약 판매인지 바로 판매인지 판별
            return sellDiscrimination(member, price, stockAsBi, stockCount, companyId);
        }
    }

    // 매도 판별해서 실행
    public StockOrder sellDiscrimination(Member member, long price, StockAsBi stockAsBi, int stockCount, long companyId) {
        // 매도 호가와 가격이 같고, 잔량이 남아 있을 경우
        if(Long.parseLong(stockAsBi.getBidp1()) == price && Integer.parseInt(stockAsBi.getBidp1()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp2()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn2()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp3()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn3()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp4()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn4()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp5()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn5()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp6()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn6()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp7()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn7()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp8()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn8()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp9()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn9()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else if(Long.parseLong(stockAsBi.getBidp10()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn10()) > stockCount)
            return sellStock(member, price, stockCount, companyId); // 판매 로직
        else
            return reserveStock(member, price, stockCount, companyId, StockOrder.OrderTypes.SELL); //예약 판매 로직
    }
    //
    //바로매도 -> 매도 확정
    public StockOrder sellStock(Member member, long price, int stockCount, long companyId) {
        // StockHold 설정 : 주식량 감소, 주식거래량 감소
        StockHold stockHold = stockHoldService.findStockHold(companyId, member.getMemberId());
        stockHold.setPrice(stockHold.getPrice() - (stockHold.getPrice() / (stockHold.getStockCount()+stockHold.getReserveStockCount())) * stockCount);
        stockHold.setStockCount(stockHold.getStockCount() - stockCount);

        // StockOrder 생성
        StockOrder stockOrder = new StockOrder();
        stockOrder.setOrderStates(StockOrder.OrderStates.ORDER_COMPLETE);
        stockOrder.setOrderTypes(StockOrder.OrderTypes.SELL);
        stockOrder.setStockCount(stockCount);
        stockOrder.setPrice(price);
        stockOrder.setCompany(companyService.findCompanyById(companyId));

         // 현금량 증가 , 연관 Entity 수정 (member 수정-> stockOrder 수정)
        List<Cash> cashList = member.getCashList();
        Cash cash = cashList.get(0);
        cash.setMoney(cash.getMoney()+(price * stockCount));
        cashList.set(0, cash);
        member.setCashList(cashList);
        stockOrder.setMember(member);
        stockOrderRepository.save(stockOrder);
        memberRepository.save(member);
        //보유량 전부 매도시 stockHold삭제  //굳이? 보유 주식 고를 떄 stockCount 나 reserveStockCount 가 없는 stockHold 데이터를 가져오면됨.
        if(stockHold.getStockCount() + stockHold.getReserveStockCount() == 0)
            stockHoldRepository.delete(stockHold);
        else
            stockHoldRepository.save(stockHold);
        return stockOrder;
    }

    // 예약 주문 처리
    public StockOrder reserveStock(Member member, long price, int stockCount, long companyId, StockOrder.OrderTypes types) {
        //예약 매도 :
        if(StockOrder.OrderTypes.SELL.equals(types)) {
            // StockHold 수정  : 시장에 내놓았으니 stockCount -> reserveStockCount
            StockHold stockHold = stockHoldService.findStockHold(companyId, member.getMemberId());
            stockHold.setStockCount(stockHold.getStockCount() - stockCount);
            stockHold.setReserveStockCount(stockCount);

        }
        //예약 매수 : 체결 대기
        StockOrder stockOrder = new StockOrder();
        stockOrder.setOrderStates(StockOrder.OrderStates.ORDER_WAIT); //체결 대기
        stockOrder.setOrderTypes(types);
        stockOrder.setStockCount(stockCount);
        stockOrder.setPrice(price);
        stockOrder.setCompany(companyService.findCompanyById(companyId));
        stockOrder.setMember(member);

        stockOrderRepository.save(stockOrder);

        return stockOrder;
    }

}

//    //예약 주문을 확인, 체결 가능한 주문을 처리
//    public void checkOrder() {
//        // 회사 리스트를 받아온다
//        List<Company> companyList = companyService.findCompanies();
//        List<StockOrder> updateBuyStockOrders = new ArrayList<>();
//        List<StockOrder> updateSellStockOrders = new ArrayList<>();
//        // for문(회사별로)
//        for(Company company : companyList) {
//            // 회사 호가 리스트를 받아온다
//            StockAsBi stockAsBi = stockAsBiService.getStockAsBi(company.getCompanyId());
//            // 회사Id에 있는 stockOrder 중 체결 대기 상태인 stockOrder를 큐로 받아온다
//            Queue<StockOrder> stockOrderQueue = getStockOrderQueue(company.getCompanyId(), StockOrder.OrderStates.ORDER_WAIT);
//            //큐가 비어있지 않으면
//            if(!stockOrderQueue.isEmpty()) {
//                // for문(큐가 다 빌 때 까지 실행한다)
//                while(!stockOrderQueue.isEmpty()) {
//                    StockOrder stockOrder = stockOrderQueue.poll();
//                    // 예약 매수 실행
//                    if(stockOrder.getOrderTypes().equals(StockOrder.OrderTypes.BUY)) {
//                        // 호가 리스트 안에 체결 대기중인 stockOrder의 조건이 맞는 것이 있으면 buyStock으로 간다
//                        StockOrder buyStock = reserveBuyDiscrimination(stockAsBi, stockOrder);
//                        // 클라이언트로 StockOrder를 보낸다(값이 있으면)
//                        if(buyStock != null)
//                            updateBuyStockOrders.add(buyStock);
//                    }
//                    // 예약 매도 실행
//                    else {
//                        // 호가 리스트 안에 체결 대기중인 stockOrder의 조건이 맞는 것이 있으면 sellStock 간다
//                        StockOrder sellStock = reserveSellDiscrimination(stockAsBi, stockOrder);
//                       // 클라이언트로 StockOrder를 보낸다(값이 있으면)
//                        if(sellStock != null)
//                            updateSellStockOrders.add(sellStock);
//                    }
//                }
//            }
//        }
//        long activeThreadCount = ManagementFactory.getThreadMXBean().getThreadCount();
//        System.out.println("현재 활성 스레드 수: " + activeThreadCount);
//        sendStockOrder(updateBuyStockOrders, updateSellStockOrders);//메시지를 전송
//        //longPollingController.notifyDataUpdated(updateBuyStockOrders, updateSellStockOrders);
//   }
//
//
//
//    private StockOrder reserveBuyDiscrimination(StockAsBi stockAsBi, StockOrder stockOrder) {
//        long price = stockOrder.getPrice();
//        int stockCount = stockOrder.getStockCount();
//        // 매도 호가와 가격이 같고, 잔량이 남아 있을 경우
//        if(Long.parseLong(stockAsBi.getAskp1()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn1()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp2()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn2()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp3()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn3()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp4()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn4()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp5()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn5()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp6()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn6()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp7()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn7()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp8()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn8()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp9()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn9()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else if(Long.parseLong(stockAsBi.getAskp10()) == price && Integer.parseInt(stockAsBi.getAskp_rsqn10()) > stockCount)
//            return reserveBuyStock(stockOrder); // 구매 로직
//        else {
//            return null; // 아무것도 안함
//        }
//
//    }
//
//    private StockOrder reserveSellDiscrimination(StockAsBi stockAsBi, StockOrder stockOrder) {
//        long price = stockOrder.getPrice();
//        int stockCount = stockOrder.getStockCount();
//        // 매도 호가와 가격이 같고, 잔량이 남아 있을 경우
//        if(Long.parseLong(stockAsBi.getBidp1()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn1()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp2()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn2()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp3()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn3()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp4()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn4()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp5()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn5()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp6()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn6()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp7()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn7()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp8()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn8()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp9()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn9()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else if(Long.parseLong(stockAsBi.getBidp10()) == price && Integer.parseInt(stockAsBi.getBidp_rsqn10()) > stockCount)
//            return reserveSellStock(stockOrder); // 판매 로직
//        else
//            return null; // 아무것도 안함
//
//    }
//
//    // 예약 매수 타결
//    public StockOrder reserveBuyStock(StockOrder stockOrder) {
//        Optional<StockOrder> optionalStockOrder = stockOrderRepository.findById(stockOrder.getStockOrderId());//굳이?
//        StockOrder updateStockOrder = optionalStockOrder.get();
//        updateStockOrder.setOrderStates(StockOrder.OrderStates.ORDER_COMPLETE); //체결 완료
//        updateStockOrder.setOrderTypes(StockOrder.OrderTypes.BUY);//굳이?
//        // StockHold 수정 : 주식량 증가, 주식거래량 증가
//        StockHold stockHold = stockHoldService.checkStockHold(stockOrder.getCompany().getCompanyId(), stockOrder.getMember().getMemberId());
//        stockHold.setStockCount(stockHold.getStockCount() + stockOrder.getStockCount());
//        stockHold.setPrice(stockHold.getPrice() + (stockOrder.getStockCount() * stockOrder.getPrice()));
//        // 현금량 감소 , 연관 Entity 수정 (member 수정-> stockOrder 수정)
//        Member member = updateStockOrder.getMember();
//        Cash cash = member.getCash();
//        cash.setMoney(cash.getMoney()-(stockOrder.getPrice() * stockOrder.getStockCount()));
//        member.setCash(cash);
//        stockOrder.setMember(member);
//        stockOrderRepository.save(stockOrder);
//        memberRepository.save(member);
//        stockHoldRepository.save(stockHold);
//
//        return stockOrder;
//    }
//
//    // 예약 매도 타결 -> 판매로 바뀔 때 금액 늘어나게, 보유 주식은 예약 할 때 줄어들도록
//    // Price 줄어드는 금액은 주식 투자금액 - (주식 투자 금액 / 보유 주식 개수) * 팔 주식 개수 (완료)
//    public StockOrder reserveSellStock(StockOrder stockOrder) {
//        Optional<StockOrder> optionalStockOrder = stockOrderRepository.findById(stockOrder.getStockOrderId());//굳이?
//        StockOrder updateStockOrder = optionalStockOrder.get();
//        updateStockOrder.setOrderStates(StockOrder.OrderStates.ORDER_COMPLETE);
//        updateStockOrder.setOrderTypes(StockOrder.OrderTypes.SELL);
//        // 보유 주식 설정 : 주식량 감소, 주식거래량 감소
//        StockHold stockHold = stockHoldService.findStockHold(stockOrder.getCompany().getCompanyId(), stockOrder.getMember().getMemberId());
//        stockHold.setPrice(stockHold.getPrice() - (stockHold.getPrice() / (stockHold.getStockCount()+stockHold.getReserveStockCount())) * stockOrder.getStockCount());
//        stockHold.setReserveStockCount(stockHold.getReserveStockCount() - stockOrder.getStockCount());
//        // 현금량 증가, 연관 Entity 수정 (member 수정-> stockOrder 수정)
//        Member member = updateStockOrder.getMember();
//        Cash cash = member.getCash();
//        cash.setMoney(cash.getMoney() + (stockOrder.getPrice() * stockOrder.getStockCount()));
//        member.setCash(cash);
//        stockOrder.setMember(member);
//        stockOrderRepository.save(stockOrder);
//        memberRepository.save(member);
//        //보유량 전부 매도시 stockHold삭제  //굳이? 보유 주식 고를 떄 stockCount 나 reserveStockCount 가 없는 stockHold 데이터를 가져오면됨.
//        if(stockHold.getStockCount() + stockHold.getReserveStockCount() == 0)
//            stockHoldRepository.delete(stockHold);
//        else
//            stockHoldRepository.save(stockHold);
//
//        return stockOrder;
//    }
//    // 미체결 예약 주문 취소
//    public void deleteStockOrder(Member member, long stockOrderId, int stockCount) {
//        //stockOrderId -> StockOrder
//        Optional<StockOrder> optionalStockOrder = stockOrderRepository.findById(stockOrderId);
//        // StockOrder 못찾으면 에러 방출
//        StockOrder stockOrder = optionalStockOrder.orElseThrow(() -> new BusinessLogicException(ExceptionCode.STOCKORDER_NOT_FOUND));
//        //StockOrder.memberId  Member.memberId 비교
//        if(stockOrder.getMember().getMemberId() != member.getMemberId()) {
//            throw new BusinessLogicException(ExceptionCode.STOCKORDER_PERMISSION_DENIED);
//        }//대기상태 아님 -> 에러 방출
//        else if(!stockOrder.getOrderStates().equals(StockOrder.OrderStates.ORDER_WAIT))
//            throw new BusinessLogicException(ExceptionCode.STOCKORDER_ALREADY_FINISH);
//        // 수량 선택해서 취소 할 수 있게(취소한 만큼 보유 주식 돌아오게) 0이 되면 미체결 스톡 오더 삭제
//        else { // 취소량이 보유량보다 작지 않으면 삭제
//            if(stockOrder.getStockCount() <= stockCount)
//                stockOrderRepository.delete(stockOrder);
//            else {
//                stockOrder.setStockCount(stockOrder.getStockCount() - stockCount);
//            }//예약 매도 인 경우 최소한 주식 수만큼 보유주식으로 돌아오게 해야함
//            if(StockOrder.OrderTypes.SELL.equals(stockOrder.getOrderTypes())) {
//                StockHold stockHold = stockHoldService.findStockHold(stockOrder.getCompany().getCompanyId(), stockOrder.getMember().getMemberId());
//                stockHold.setStockCount(stockHold.getStockCount() + stockCount);
//                stockHold.setReserveStockCount(stockHold.getReserveStockCount() - stockCount);
//            }
//        }
//    }
//    // 클라언트에 알림 보냄
//    //주식 주문이 업데이트 -> 메시지를 전송
//    public void sendStockOrder(List<StockOrder> updateBuyStockOrders, List<StockOrder> updateSellStockOrders) {
//        // 회원 ID별로 매수 주문을 그룹화합니다.
//        Map<Long, List<StockOrder>> buyStockOrdersByMemberId = updateBuyStockOrders.stream()
//                .collect(Collectors.groupingBy(stockOrder -> stockOrder.getMember().getMemberId()));
//        // 회원 ID별로 매도 주문을 그룹화합니다.
//        Map<Long, List<StockOrder>> sellStockOrdersByMemberId = updateSellStockOrders.stream()
//                .collect(Collectors.groupingBy(stockOrder -> stockOrder.getMember().getMemberId()));
//
//        // 매수 주문을 회원 ID별로 순회하며 메시지를 보냅니다.
//        buyStockOrdersByMemberId.forEach((memberId, orders) -> {
//            System.out.println("Member ID: " + memberId);
//            orders.forEach(order -> {
//                String destination = "/sub/" + memberId;
//                StockOrder stockOrder = order;
//                StockOrderResponseDto stockOrderResponseDto = stockMapper.stockOrderToStockOrderResponseDto(stockOrder);
//                messagingTemplate.convertAndSend(destination, stockOrderResponseDto);//메시지 전송
//            });
//        });
//        // 매도 주문을 회원 ID별로 순회하며 메시지를 보냅니다.
//        sellStockOrdersByMemberId.forEach((memberId, orders) -> {
//            orders.forEach(order -> {
//                String destination = "/sub/" + memberId;
//                System.out.println(destination);
//                StockOrder stockOrder = order;
//                StockOrderResponseDto stockOrderResponseDto = stockMapper.stockOrderToStockOrderResponseDto(stockOrder);
//                messagingTemplate.convertAndSend(destination, stockOrderResponseDto);//메시지 전송
//            });
//        });
//    }
//    // 거래 대기중인 매수 StockOrder Queue형태로 불러오기
//    public Queue<StockOrder> getStockOrderQueue(long companyId, StockOrder.OrderStates orderStates) {
//        List<StockOrder> stockOrderList = stockOrderRepository.findAllByCompanyCompanyIdAndOrderStates(companyId, orderStates);
//        Queue<StockOrder> stockOrderQueue = new LinkedList<>(stockOrderList);
//        return stockOrderQueue;
//    }
//
//    // 멤버의 모든 주식 거래내역 삭제하기
//    public void deleteStockOrders(Member member) {
//        List<StockOrder> stockOrders = stockOrderRepository.findAllByMember_MemberId(member.getMemberId());
//        stockOrderRepository.deleteAll(stockOrders);
//    }
//
//    // 멤버의 모든 StockOrders 불러오기
//    public List<StockOrderResponseDto> getMemberStockOrders(long memberId) {
//        List<StockOrder> stockOrders = stockOrderRepository.findAllByMember_MemberIdOrderByModifiedAtDesc(memberId);
//        List<StockOrderResponseDto> stockOrderRepositories = stockOrders.stream()
//                .map(stockOrder -> stockMapper.stockOrderToStockOrderResponseDto(stockOrder)).collect(Collectors.toList());
//
//        return stockOrderRepositories;
//    }
