package com.stocktide.stocktideserver.scheduler;

import com.stocktide.stocktideserver.stock.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockScheduler {
    private final StockAsBiService stockAsBiService;
    private final StockMinService stockMinService;
    private final CompanyService companyService;
    private final TokenService tokenService;
    private final StockOrderService stockOrderService;

    @Scheduled(cron = "0 30 9-15 * * MON-FRI")
    public void myScheduledStockAsBiMethod() throws InterruptedException {
        stockAsBiService.updateStockAsBi();
    }

    @Scheduled(cron = "0 0 10-15 * * MON-FRI")
    public void myScheduledStockAsBiMethod2() throws InterruptedException {
        stockAsBiService.updateStockAsBi();
    }

    @Scheduled(cron = "0 30 9-15 * * MON-FRI")
    public void myScheduledStockMinMethod() throws InterruptedException {
        stockMinService.updateStockMin();
    }
    @Scheduled(cron = "0 0 10-15 * * MON-FRI")
    public void myScheduledStockMinMethod2() throws InterruptedException {
        stockMinService.updateStockMin();

    }
}
// 0: 초 (초 단위로 0초에 실행)
// 30: 분 (분 단위로 30분에 실행)
// 9-15: 시간 (오전 9시부터 오후 3시까지 매 시간 실행)
// *: 일 (월별 날짜로 모든 날에 실행)
// *: 월 (모든 달에 실행)
// MON-FRI: 요일 (월요일부터 금요일까지 실행)
// 매주 월요일부터 금요일까지 9:30부터 15:30까지 30분 간격으로 실행됩니다.

//    private final WebSocketController webSocketController;
//    @Scheduled(cron = "10 41 1-23 * * MON-FRI")
//    public void asbi1() throws InterruptedException {
//        log.info("---------------asbi1  started----------------------------------------");
//        stockAsBiService.updateStockAsBi();
////        stockOrderService.checkOrder();
//        log.info("---------------asbi1  finished----------------------------------------");
//    }
//
//    @Scheduled(cron = "10 50 9-15 * * MON-FRI")
//    public void min1() throws InterruptedException {
//        log.info("---------------min1  started----------------------------------------");
//        stockMinService.updateStockMin();
//        log.info("---------------min1  finished----------------------------------------");
//    }



//@Scheduled(cron = "0 30 9-15 * * MON-FRI")
//public void myScheduledStockAsBiMethod() throws InterruptedException {
//    //매주 월요일부터 금요일까지 9:30부터 15:30까지 매 30분에 작업을 실행합니다.
////        LocalDateTime start = LocalDateTime.now();
//    stockAsBiService.updateStockAsBi();
////        stockOrderService.checkOrder();
////        LocalDateTime end = LocalDateTime.now();
////        Duration duration = Duration.between(start, end);
////        System.out.println(duration.getSeconds());
//}
//
//@Scheduled(cron = "0 0 10-15 * * MON-FRI")
//public void myScheduledStockAsBiMethod2() throws InterruptedException {
//    //매주 월요일부터 금요일까지 오전 10시부터 오후 3시까지 매 정각에 작업을 실행합니다.
////        LocalDateTime start = LocalDateTime.now();
//    stockAsBiService.updateStockAsBi();
////        stockOrderService.checkOrder();
////        LocalDateTime end = LocalDateTime.now();
////        Duration duration = Duration.between(start, end);
////        System.out.println(duration.getSeconds());
//
//}
//
//@Scheduled(cron = "0 30 9-15 * * MON-FRI")
//public void myScheduledStockMinMethod() throws InterruptedException {
//    //매주 월요일부터 금요일까지 9:30부터 15:30까지 30분 간격으로 실행됩니다.
////        LocalDateTime start = LocalDateTime.now();
//    stockMinService.updateStockMin();
////        LocalDateTime end = LocalDateTime.now();
////        Duration duration = Duration.between(start, end);
////        System.out.println(duration.getSeconds());
//}
//
//@Scheduled(cron = "0 0 10-15 * * MON-FRI")
//public void myScheduledStockMinMethod2() throws InterruptedException {
//    //매주 월요일부터 금요일까지 오전 10시부터 오후 3시까지 매 정각에 작업을 실행합니다.
////        LocalDateTime start = LocalDateTime.now();
//    stockMinService.updateStockMin();
////        LocalDateTime end = LocalDateTime.now();
////        Duration duration = Duration.between(start, end);
////        System.out.println(duration.getSeconds());
//
//}








//    @Scheduled(fixedRate = 1000)
//    public void run() throws InterruptedException {
//        webSocketController.check();
//    }

//    @Scheduled(fixedRate = 10000000)
//    public void secondSchedule() throws InterruptedException {
//        LocalDateTime start = LocalDateTime.now();
//        stockAsBiService.updateStockAsBi();
//        stockMinService.updateStockMin();
//        LocalDateTime end = LocalDateTime.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println(duration.getSeconds());
//
//    }

//    @Scheduled(fixedRate = 10000000)
//    public void firstSchedule() throws InterruptedException {
//        LocalDateTime start = LocalDateTime.now();
//        stockAsBiService.updateStockAsBi();
//        LocalDateTime end = LocalDateTime.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println(duration.getSeconds());
//    }

//    @Scheduled(fixedRate = 10000000)
//    public void secondSchedule() throws InterruptedException {
//        LocalDateTime start = LocalDateTime.now();
//        stockMinService.updateStockMin();
//        LocalDateTime end = LocalDateTime.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println(duration.getSeconds());
//    }

/////////////////////////////

//    @Scheduled(fixedRate = 10000000)
//    public void secondSchedule() throws InterruptedException {
//        LocalDateTime start = LocalDateTime.now();
//        stockMinService.updateStockMin();
//        LocalDateTime end = LocalDateTime.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println(duration.getSeconds());
//    }

//    @Scheduled(fixedRate = 10000000)
//    public void secondSchedule() throws InterruptedException {
//        LocalDateTime start = LocalDateTime.now();
//        stockAsBiService.updateStockAsBi();
//        stockMinService.updateStockMin();
//        LocalDateTime end = LocalDateTime.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println(duration.getSeconds());
//    }

//    @Scheduled(fixedRate = 60000)
//    public void run() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run1");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }
//
//    @Scheduled(fixedRate = 60000, initialDelay = 1000)
//    public void run2() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run2");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }
//
//    @Scheduled(fixedRate = 60000, initialDelay = 2000)
//    public void run3() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run3");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }
//
//    @Scheduled(fixedRate = 60000, initialDelay = 3000)
//    public void run4() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run4");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }
//
//    @Scheduled(fixedRate = 60000, initialDelay = 4000)
//    public void run5() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run5");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }
//
//    @Scheduled(fixedRate = 60000, initialDelay = 5000)
//    public void run6() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run6");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }
//
//    @Scheduled(fixedRate = 60000, initialDelay = 6000)
//    public void run7() {
//        List<StockasbiDataDto> stockasbiDataDtos = stockService.getStockasbiData();
//
//        System.out.println("run7");
//
//        for(StockasbiDataDto stockasbiDataDto : stockasbiDataDtos) {
//            log.info(stockasbiDataDto.getOutput1().getAskp1());
//            log.info(stockasbiDataDto.getOutput1().getAskp2());
//            log.info(stockasbiDataDto.getOutput1().getAskp3());
//            log.info(stockasbiDataDto.getOutput1().getAskp4());
//            log.info(stockasbiDataDto.getOutput1().getAskp5());
//        }
//    }

