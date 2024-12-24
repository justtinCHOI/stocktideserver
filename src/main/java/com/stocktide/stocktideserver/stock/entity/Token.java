package com.stocktide.stocktideserver.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {
    @Id
    long tokenId;

    @Column(length = 500)
    private String token;

    @Column
    private LocalDateTime expired;


}
