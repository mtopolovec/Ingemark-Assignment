package com.ingemark.assignment.assignment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currency;
    private BigDecimal buyingRate;
    private BigDecimal middleRate;
    private BigDecimal sellingRate;

    private LocalDate date;

    public ExchangeRate(String currency, BigDecimal buyingRate, BigDecimal middleRate, BigDecimal sellingRate, LocalDate date) {
        this.currency = currency;
        this.buyingRate = buyingRate;
        this.middleRate = middleRate;
        this.sellingRate = sellingRate;
        this.date = date;
    }
}
