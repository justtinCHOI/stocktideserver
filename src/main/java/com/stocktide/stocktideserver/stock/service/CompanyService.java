package com.stocktide.stocktideserver.stock.service;

import com.stocktide.stocktideserver.stock.dto.CompanyModifyDTO;
import com.stocktide.stocktideserver.stock.dto.StockasbiDataDto;
import com.stocktide.stocktideserver.stock.entity.Company;
import com.stocktide.stocktideserver.stock.entity.StockAsBi;
import com.stocktide.stocktideserver.stock.mapper.ApiMapper;
import com.stocktide.stocktideserver.stock.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final ApiCallService apiCallService;
    private final ApiMapper apiMapper;

    //code -> Company
    public Company findCompanyByCode(String stockCode) {
        return companyRepository.findByCode(stockCode);
    }

    //autoIncrement -> Company
    public Company findCompanyById(long companyId) {
        log.info("----------------------------------findCompanyById started");
        Company company = companyRepository.findByCompanyId(companyId);
        log.info("----------------------------------findCompanyById finished {}", company);
        return company;
    }

    // 모든 회사 리턴
    public List<Company> findCompanies() {
        log.info("---------------findCompanies  started----------------------------------------");
        List<Company> all = companyRepository.findAll();
        log.info("---------------findCompanies  finished {}----------------------------------------", all);
        return all;
    }


    // 특정 회사 저장
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    // 모든 회사 저장
    public List<Company> saveCompanies(List<Company> companies) {
        return companyRepository.saveAll(companies);
    }

    public void fillCompany() {
        Company company = new Company();

    }

    //회사들의 의 korName, code를 채우고 데이터베이스에 저장
    public void fillCompaines() throws InterruptedException {
        List<String> korName = List.of("삼성전자", "POSCO홀딩스", "셀트리온", "에코프로", "에코프로비엠", "디와이", "쿠쿠홀딩스", "카카오뱅크", "한세엠케이", "KG케미칼", "LG화학", "현대차", "LG전자", "기아");
        List<String> code = List.of("005930", "005490", "068270", "086520", "247540", "013570", "192400", "323410", "069640", "001390", "051910", "005380", "066570", "000270");

        for(int i = 0; i < code.size(); i++) {
            Company company = new Company();
            company.setCode(code.get(i));
            company.setKorName(korName.get(i));
            company.setStockAsBi(new StockAsBi());

            StockasbiDataDto stockasbiDataDto = apiCallService.getStockasbiDataFromApi(company.getCode());
            //StockasbiDataDto -> StockAsBiOutput1 -> StockAsBi
            StockAsBi stockAsBi = apiMapper.stockAsBiOutput1ToStockAsBi(stockasbiDataDto.getOutput1());
            
            //양방향 관계이므로 서로에 저장
            company.setStockAsBi(stockAsBi);
            stockAsBi.setCompany(company);

            Thread.sleep(500);

            companyRepository.save(company);
        }
    }

    public void modify(CompanyModifyDTO companyModifyDTO) {

        Optional<Company> result = companyRepository.findById(companyModifyDTO.getCompanyId());

        Company company = result.orElseThrow();

        company.setCode(companyModifyDTO.getCode());
        company.setKorName(companyModifyDTO.getKorName());
//        company.setCreatedAt(companyModifyDTO.getCreatedAt());

        companyRepository.save(company);

    }
}


