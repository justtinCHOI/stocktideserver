package com.stocktide.stocktideserver.stock.service;

import com.stocktide.stocktideserver.exception.BusinessLogicException;
import com.stocktide.stocktideserver.exception.ExceptionCode;
import com.stocktide.stocktideserver.stock.dto.StockasbiDataDto;
import com.stocktide.stocktideserver.stock.entity.Company;
import com.stocktide.stocktideserver.stock.entity.StockAsBi;
import com.stocktide.stocktideserver.stock.mapper.ApiMapper;
import com.stocktide.stocktideserver.stock.repository.StockAsBiRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class StockAsBiService {

    private final StockAsBiRepository stockAsBiRepository;
    private final ApiCallService apiCallService;
    private final ApiMapper apiMapper;
    private final CompanyService companyService;

    public StockAsBiService(StockAsBiRepository stockAsBiRepository, ApiCallService apiCallService, ApiMapper apiMapper, CompanyService companyService) {
        this.stockAsBiRepository = stockAsBiRepository;
        this.apiCallService = apiCallService;
        this.apiMapper = apiMapper;
        this.companyService = companyService;
    }

    //StockAsBi 저장
    public StockAsBi saveStockAsBi(StockAsBi stockAsBi) {
        return stockAsBiRepository.save(stockAsBi);
    }

    //저장되어 있는 회사코드 -> AsBi 정보 update

    public void updateStockAsBi() throws InterruptedException {
        log.info("---------------updateStockAsBi  started----------------------------------------");
        List<Company> companyList = companyService.findCompanies(); //모든 회사
        log.info("---------------companyList {}----------------------------------------", companyList);

        for(int i = 0; i < companyList.size(); i++) {
            log.info("---------------{}st company  started----------------------------------------", (i + 1));
            log.info("---------------{}st company  code----------------------------------------", companyList.get(i).getCode());
            // 주식 코드로 회사 불러오기
            Company company = companyService.findCompanyByCode(companyList.get(i).getCode());
            log.info("---------------{}st company ----------------------------------------", company);
            // 해당 회사의 asbi api호출하기
            StockasbiDataDto stockasbiDataDto = apiCallService.getStockasbiDataFromApi(company.getCode());
            // StockasbiDataDto -> StockAsBiOutput1 -> StockAsBi
            StockAsBi stockAsBi = apiMapper.stockAsBiOutput1ToStockAsBi(stockasbiDataDto.getOutput1());
            log.info("---------------{}st company stockAsBi :  ----------------------------------------", stockAsBi.getAskp1());
            // 새로운 stockAsBi의 회사 등록
            stockAsBi.setCompany(company);
            log.info("---------------{}st company setCompany :  ----------------------------------------", stockAsBi.getCompany().getCompanyId());
            // 호가 컬럼을 새로운 호가 컬럼으로 변경
            StockAsBi oldStockAsBi = company.getStockAsBi();
            if(oldStockAsBi == null) {
                stockAsBiRepository.save(stockAsBi);
            }else{
                stockAsBi.setStockAsBiId(oldStockAsBi.getStockAsBiId());
                stockAsBiRepository.save(stockAsBi);
            }
            company.setStockAsBi(stockAsBi);
            // 저장
            companyService.saveCompany(company);
            log.info("---------------companyService.saveCompany {} ----------------------------------------", company.getStockAsBi().getAskp1());
//            Thread.sleep(500);
            log.info("---------------updateStockAsBi  finished----------------------------------------");
        }
    }
    //companyId -> 회사의 StockAsBi 정보
    public StockAsBi getStockAsBi(long companyId) {
        Optional<StockAsBi> stock = stockAsBiRepository.findById(companyId);
        stock.orElseThrow(() -> new BusinessLogicException(ExceptionCode.STOCKASBI_NOT_FOUND));
        return stock.get();
    }

}
