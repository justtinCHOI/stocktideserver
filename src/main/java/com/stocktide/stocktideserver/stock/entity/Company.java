package com.stocktide.stocktideserver.stock.entity;

import com.stocktide.stocktideserver.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long companyId;

    @Column
    private String code;

    @Column
    private String korName;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private StockAsBi stockAsBi;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private StockInf stockInf;

}
