package com.ingemark.assignment.assignment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_eur", nullable = false)
    private BigDecimal priceEur;

    @Column(name = "price_usd", nullable = false)
    private BigDecimal priceUsd;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    public Product(String name, BigDecimal priceEur, BigDecimal middleUsdRate, Boolean isAvailable) {
        this.name = name;
        this.code = createCode();
        this.priceEur = priceEur;
        this.priceUsd = priceEur.multiply(middleUsdRate);
        this.isAvailable = isAvailable;
    }

    private String createCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder serialNumber = new StringBuilder(10);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(chars.length());
            serialNumber.append(chars.charAt(randomIndex));
        }

        return serialNumber.toString();
    }
}


