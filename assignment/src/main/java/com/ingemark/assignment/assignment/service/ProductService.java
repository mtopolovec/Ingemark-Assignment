package com.ingemark.assignment.assignment.service;

import com.ingemark.assignment.assignment.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getAllProductsWithStatus(Boolean status);
    ProductDTO getProductById(Long id);
    ProductDTO addProduct(ProductDTO item);
    ProductDTO updateProduct(Long id, ProductDTO item);
    void deleteProduct(Long id);
}
