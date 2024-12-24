package com.stocktide.stocktideserver.stock.repository;

import com.stocktide.stocktideserver.stock.entity.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//interface 를 만들어주면 자동으로 코드가 만들어진다.
public interface CompanyRepository extends JpaRepository<Company, Long> {

//    @Query("SELECT c FROM Company c JOIN FETCH c.stockAsBi JOIN FETCH c.stockInf WHERE c.code = :code")
    @Query("SELECT c FROM Company c WHERE c.code = :code")
    Company findByCode(@Param("code") String code);

//    @Query("SELECT c FROM Company c JOIN FETCH c.stockAsBi JOIN FETCH c.stockInf WHERE c.companyId = :companyId")
    @Query("SELECT c FROM Company c WHERE c.companyId = :companyId")
    Company findByCompanyId(@Param("companyId") long companyId);

    @Query("SELECT c from Company c")
    @EntityGraph(attributePaths = {"stockAsBi", "stockInf"})
    List<Company> findAll();
}
