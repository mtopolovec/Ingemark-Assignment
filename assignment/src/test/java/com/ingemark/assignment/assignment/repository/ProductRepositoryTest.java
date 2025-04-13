package com.ingemark.assignment.assignment.repository;

import com.ingemark.assignment.assignment.model.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product firstTestProduct;
    private Product secondTestProduct;
    private final BigDecimal middleUsdRate = new BigDecimal("1.1");


    @BeforeEach
    public void setUp() {
        firstTestProduct = new Product("First test product", new BigDecimal("19.99"), middleUsdRate, true);
        secondTestProduct = new Product("Second test product", new BigDecimal("29.99"), middleUsdRate, false);
        productRepository.saveAll(List.of(firstTestProduct, secondTestProduct));
    }

    @Test
    void findByName_isSuccessful() {
        String productName = "First test product";
        List<Product> products = productRepository.findByName(productName);
        assertThat(products).hasSize(1);
        assertThat(products.getFirst().getName()).isEqualTo(productName);
        assertThat(products.getFirst().getPriceUsd()).isEqualTo(firstTestProduct.getPriceUsd());
        assertThat(products.getFirst().getIsAvailable()).isTrue();
        assertThat(products.getFirst().getName()).isNotEqualTo(secondTestProduct.getName());
    }

    @Test
    public void findByName_NoResult() {
        List<Product> products = productRepository.findByName("Non-existent product");
        assertThat(products).isEmpty();
    }

    @Test
    void findByIsAvailable_isSuccessful() {
        List<Product> availableProducts = productRepository.findByIsAvailable(true);
        assertThat(availableProducts).hasSize(1);
        assertThat(availableProducts.getFirst().getIsAvailable()).isTrue();
        assertThat(availableProducts.getFirst().getName()).isEqualTo(firstTestProduct.getName());
        assertThat(availableProducts.getFirst().getPriceUsd()).isEqualTo(firstTestProduct.getPriceUsd());

        List<Product> unavailableProducts = productRepository.findByIsAvailable(false);
        assertThat(unavailableProducts).hasSize(1);
        assertThat(unavailableProducts.getFirst().getIsAvailable()).isFalse();
        assertThat(unavailableProducts.getFirst().getName()).isEqualTo(secondTestProduct.getName());
        assertThat(unavailableProducts.getFirst().getPriceUsd()).isEqualTo(secondTestProduct.getPriceUsd());
    }


}