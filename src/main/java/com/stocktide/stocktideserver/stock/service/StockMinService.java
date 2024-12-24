package com.stocktide.stocktideserver.stock.service;

import com.stocktide.stocktideserver.stock.dto.StockMinDto;
import com.stocktide.stocktideserver.stock.dto.StockMinResponseDto;
import com.stocktide.stocktideserver.stock.entity.Company;
import com.stocktide.stocktideserver.stock.entity.StockInf;
import com.stocktide.stocktideserver.stock.entity.StockMin;
import com.stocktide.stocktideserver.stock.mapper.ApiMapper;
import com.stocktide.stocktideserver.stock.mapper.StockMapper;
import com.stocktide.stocktideserver.stock.repository.StockInfRepository;
import com.stocktide.stocktideserver.stock.repository.StockMinRepository;
import com.stocktide.stocktideserver.util.Time;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StockMinService {

    private final CompanyService companyService;
    private final ApiCallService apiCallService;
    private final ApiMapper apiMapper;
    private final StockMinRepository stockMinRepository;
    private final StockMapper stockMapper;
    private final StockInfRepository stockInfRepository;

    // 모든 회사의 정호, 주식(StockInf, StockMin) with API -> 데이터베이스에 저장
    public void updateStockMin() throws InterruptedException {
        log.info("---------------updateStockMin  started----------------------------------------");
        List<Company> companyList = companyService.findCompanies();
        LocalDateTime now = LocalDateTime.now();
        String strHour = Time.strHour(now);

        //Company -> code + strHour -> StockMinDto ->  List<StockMinOutput2> -> List<StockMin> -> 정렬 -> 저장
        //Company -> code + strHour -> StockMinDto ->  StockMinOutput1 -> StockInf 저장 -> Company
        for(int i = 0; i < companyList.size(); i++) {
            // 주식 코드로 회사 불러오기
            Company company = companyService.findCompanyByCode(companyList.get(i).getCode());
            // 분봉 api 호출하기
            StockMinDto stockMinDto = apiCallService.getStockMinDataFromApi(company.getCode(), strHour);
            // mapper로 정리 된 값 받기
            List<StockMin> stockMinList = stockMinDto.getOutput2().stream()
                    .map(stockMinOutput2 -> {
                        StockMin stockMin = apiMapper.stockMinOutput2ToStockMin(stockMinOutput2);
                        stockMin.setCompany(company);
                        stockMin.setStockTradeTime(now);
                        return stockMin;
                    }).collect(Collectors.toList());
            // 빠른 시간 순으로 정렬
            Collections.sort(stockMinList, Comparator.comparing(StockMin::getStockTradeTime));
            // 회사 정보 저장
            StockInf stockInf = apiMapper.stockMinOutput1ToStockInf(stockMinDto.getOutput1());
            stockInf.setCompany(company);
            StockInf oldStockInf = company.getStockInf();
            if(oldStockInf != null){
                stockInf.setStockInfId(oldStockInf.getStockInfId());
            }
            stockInfRepository.save(stockInf);
            company.setStockInf(stockInf);
            // 저장한다
            stockMinRepository.saveAll(stockMinList);
            companyService.saveCompany(company);

//            Thread.sleep(500);
            log.info("---------------updateStockMin  finished----------------------------------------");

        }
    }

    // companyId -> List<StockMin> 
    public List<StockMin> getChart(long companyId) {
        List<StockMin> stockMinList = stockMinRepository.findAllByCompanyCompanyId(companyId);

        return stockMinList;
    }
    //StockMin 420개 리스트 -> StockMinResponseDto 420개 리스트 , 내림차순 -> 오름차순
    public List<StockMinResponseDto> getRecent420StockMin(long companyId) {
        // findTop420ByCompanyIdOrderByStockMinIdDesc() : 최신 420개의 주식 분봉 데이터
        List<StockMin> stockMinList = stockMinRepository.findTop420ByCompanyIdOrderByStockMinIdDesc(companyId);

        List<StockMinResponseDto> stockMinResponseDtos = stockMinList.stream()
                .map(stockMin -> stockMapper.stockMinToStockMinResponseDto(stockMin)).collect(Collectors.toList());
        Collections.reverse(stockMinResponseDtos);
        return stockMinResponseDtos;
    }

}
