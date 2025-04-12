package com.ingemark.assignment.assignment.mapper;

import com.ingemark.assignment.assignment.dto.ProductDTO;
import com.ingemark.assignment.assignment.model.Product;

import java.math.BigDecimal;

public class ProductMapper {
    public static ProductDTO productToDto(Product product) {
        return new ProductDTO(
                product.getName(),
                product.getPriceEur(),
                product.getIsAvailable()
        );
    }
    public static Product dtoToProduct(ProductDTO productDTO, BigDecimal currencyMiddleRate) {
        return new Product(
                productDTO.getName(),
                productDTO.getPriceEur(),
                currencyMiddleRate,
                productDTO.getIsAvailable()
                );
    }
}
