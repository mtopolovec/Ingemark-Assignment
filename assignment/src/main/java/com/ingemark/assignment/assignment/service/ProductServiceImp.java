package com.ingemark.assignment.assignment.service;

import com.ingemark.assignment.assignment.dto.ProductDTO;
import com.ingemark.assignment.assignment.mapper.ProductMapper;
import com.ingemark.assignment.assignment.model.Product;
import com.ingemark.assignment.assignment.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import org.hibernate.FetchNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final FetchHnbData fetchHnbData;

    public ProductServiceImp(ProductRepository productRepository, FetchHnbData fetchHnbData) {
        this.productRepository = productRepository;
        this.fetchHnbData = fetchHnbData;
    }

    private BigDecimal getCurrencyMiddleRate() {
        return fetchHnbData.fetchData().getMiddleRate();
    }

    private void checkIfProductNameExists(String name) {
        if (!productRepository.findByName(name).isEmpty()) {
            throw new EntityExistsException("There is a product with that name already!");
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::productToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getAllProductsWithStatus(Boolean status) {
        return productRepository.findByIsAvailable(status)
                .stream()
                .map(ProductMapper::productToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::productToDto)
                .orElseThrow(() -> new FetchNotFoundException("There is no product with this Id: " + id + ".", null));
    }

    @Override
    public ProductDTO addProduct(ProductDTO item) {
        checkIfProductNameExists(item.getName());
        Product savedProduct = productRepository.save(ProductMapper.dtoToProduct(item, getCurrencyMiddleRate()));
        return ProductMapper.productToDto(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO item) {
        Product updatedProduct;
        if (productRepository.existsById(id)) {
            Optional<Product> existingProduct = productRepository.findById(id);
            updatedProduct = ProductMapper.dtoToProduct(item, getCurrencyMiddleRate());
            updatedProduct.setId(id);
            updatedProduct.setCode(existingProduct.get().getCode());
        } else {
            throw new FetchNotFoundException("There is no product with this Id: " + id + ".", null);
        }
        checkIfProductNameExists(item.getName());
        productRepository.saveAndFlush(updatedProduct);
        return ProductMapper.productToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new FetchNotFoundException("There is no product with this Id: " + id + ".", null);
        }
    }

}
