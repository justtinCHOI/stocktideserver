package com.stocktide.stocktideserver.repository;

import com.stocktide.stocktideserver.stock.repository.CompanyRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@Disabled
@SpringBootTest
@Log4j2
//@ActiveProfiles("test")
public class CompanyRepositoryTests {

    @Autowired
    private CompanyRepository companyRepository;

//    @Test
//    public void test1() {
//        assertNotNull(companyRepository);
//        log.info(companyRepository.getClass().getName() + " mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
//    }

}
