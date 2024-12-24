package com.stocktide.stocktideserver.stock.controller;

import com.stocktide.stocktideserver.stock.dto.CompanyModifyDTO;
import com.stocktide.stocktideserver.stock.dto.CompanyResponseDto;
import com.stocktide.stocktideserver.stock.dto.StockMinResponseDto;
import com.stocktide.stocktideserver.stock.entity.Company;
import com.stocktide.stocktideserver.stock.mapper.StockMapper;
import com.stocktide.stocktideserver.stock.repository.CompanyRepository;
import com.stocktide.stocktideserver.stock.service.CompanyService;
import com.stocktide.stocktideserver.stock.service.StockMinService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;
    private final StockMapper stockMapper;
    private final CompanyRepository companyRepository;
    private StockMinService stockMinService;

    // 전체 회사 리스트
    @GetMapping("/list")
    public ResponseEntity<List<CompanyResponseDto>> getCompanyList() {

        List<Company> companyList = companyRepository.findAll();

        List<CompanyResponseDto> companyResponseDtoList = stockMapper.CompaniesToCompanyResponseDtos(companyList);

        return new ResponseEntity<>(companyResponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable("companyId") Long companyId) {

        Company company = companyService.findCompanyById(companyId);

        CompanyResponseDto companyResponseDto = stockMapper.companyToCompanyResponseDto(company);

        return new ResponseEntity<>(companyResponseDto, HttpStatus.OK);
    }

    // 차트 하나 호출
    @GetMapping("/charts/{companyId}")
    public ResponseEntity<List<StockMinResponseDto>> getCompanyChart(@PathVariable("companyId") long companyId) {
        List<StockMinResponseDto> stockMinList = stockMinService.getRecent420StockMin(companyId);

        return new ResponseEntity<>(stockMinList, HttpStatus.OK);
    }

    //회사 수정
    @PutMapping("/{companyId}")
    public Map<String, String> modify(@PathVariable("companyId") Long companyId,
                                      @RequestBody CompanyModifyDTO companyModifyDTO) {
        companyModifyDTO.setCompanyId(companyId);
        companyService.modify(companyModifyDTO);
        return Map.of("RESULT", "SUCCESS");
    }

}
