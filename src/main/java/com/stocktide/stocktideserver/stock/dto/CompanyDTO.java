package com.stocktide.stocktideserver.stock.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long companyId;

    @Column
    private String code;

    @Column
    private String korName;

    @Column
    private LocalDate created_at;

//    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
//    private StockAsBi stockAsBi;
//
//    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
//    private StockInf stockInf;
}
