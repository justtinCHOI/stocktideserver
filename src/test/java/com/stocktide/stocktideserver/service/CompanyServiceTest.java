package com.stocktide.stocktideserver.service;

import com.stocktide.stocktideserver.stock.entity.Company;
import com.stocktide.stocktideserver.stock.repository.CompanyRepository;
import com.stocktide.stocktideserver.stock.service.CompanyService;
import com.stocktide.stocktideserver.stock.service.StockAsBiService;
import com.stocktide.stocktideserver.stock.service.StockMinService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
//@ActiveProfiles("test")
public class CompanyServiceTest {

    @Autowired
    CompanyService companyService;

    @Autowired
    StockMinService stockMinService;

    @Autowired
    StockAsBiService stockAsBiService;
    @Autowired
    private CompanyRepository companyRepository;
    // testRegister
    // 회사 1개 추가 ->
    // testFillCompaines
    // 회사 2 ~ 15 Company + StockAsBi 추가 ->
    // testUpdateStockMin
    // 회사 2 ~ 15 StockMin 추가

    // create first company
    // If you finished with updating, remove comment on @Transactional
    @Test
    @Transactional
    public void testRegister() {
        Company company = Company.builder()
                .code("Code123")
                .korName("Korean Name")
//                .created_at(LocalDate.of(2024, 5, 4))
                .build();
        Company savedCompany = companyService.saveCompany(company);
        log.info("savedCompany {}", savedCompany);
    }

    // update Company + StockAsBi
    // If you finished with updating, remove comment on @Transactional
    @Test
    @Transactional
    public void testFillCompaines() throws InterruptedException {
        companyService.fillCompaines();
    }

    // build.gradle 할 때 실행 되므로 만료되지 않는 accessToken 을 계속 요청
    // update StockMin
    // If you finished with updating, remove comment on @Transactional
    @Test
    @Transactional
    public void testUpdateStockMin() throws InterruptedException {
        stockMinService.updateStockMin();
    }

    @Test
    @Transactional
    public void testFindCompanies()  {
        companyService.findCompanies();
    }

    @Test
    public void testGetCompanyId() {
        long companyId = 1L;
        Company company = companyRepository.findByCompanyId(companyId);
        log.info(company.getCompanyId());
    }



//    @Test
//    public void testGetList() {
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(11).build();
//        log.info(companyService.getList(pageRequestDTO));
//    }
}
