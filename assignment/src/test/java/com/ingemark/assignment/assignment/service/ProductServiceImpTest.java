package com.ingemark.assignment.assignment.service;

import com.ingemark.assignment.assignment.dto.ProductDTO;
import com.ingemark.assignment.assignment.mapper.ProductMapper;
import com.ingemark.assignment.assignment.model.HnbResponse;
import com.ingemark.assignment.assignment.model.Product;
import com.ingemark.assignment.assignment.repository.ProductRepository;
import jakarta.persistence.EntityExistsException;
import org.hibernate.FetchNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FetchHnbData fetchHnbData;

    @InjectMocks
    private ProductServiceImp productService;

    private final BigDecimal mockRate = new BigDecimal("1.1");
    String firstName = "Test first name";
    String secondName = "Test second name";
    String thirdName = "Test third name";
    Product firstProduct;
    Product secondProduct;
    Product thirdProduct;

    String productDTOName = "Testing DTO name";
    ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        firstProduct = createProduct(firstName, new BigDecimal("19.99"), mockRate, true);
        secondProduct = createProduct(secondName, new BigDecimal("29.99"), mockRate, false);
        thirdProduct = createProduct(thirdName, new BigDecimal("199.99"), mockRate, true);
        productDTO = createProductDTO(productDTOName, new BigDecimal("999.99"), true);
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(firstProduct, secondProduct, thirdProduct));

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(firstName, result.getFirst().getName());
        assertEquals(secondName, result.get(1).getName());
        assertEquals(thirdName, result.getLast().getName());
    }

    @Test
    void getAllProductsWithStatus() {
        when(productRepository.findAll()).thenReturn(List.of(firstProduct, secondProduct, thirdProduct));
        when(productRepository.findByIsAvailable(true)).thenReturn(List.of(firstProduct, thirdProduct));

        List<ProductDTO> allProducts = productService.getAllProducts();
        List<ProductDTO> result = productService.getAllProductsWithStatus(true);

        assertEquals(3, allProducts.size());
        assertEquals(2, result.size());
        assertTrue(result.getFirst().getIsAvailable());
        assertTrue(result.getLast().getIsAvailable());
    }

    @Test
    void getProductById() {
        when(productRepository.findById(3L)).thenReturn(Optional.of(thirdProduct));

        ProductDTO result = productService.getProductById(3L);

        assertEquals(thirdName, result.getName());
    }

    @Test
    void getProductById_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FetchNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void addProduct() {
        HnbResponse mockResponse = new HnbResponse();
        mockResponse.setMiddleRate(mockRate);

        when(productRepository.findByName(productDTOName)).thenReturn(List.of());
        when(fetchHnbData.fetchData()).thenReturn(mockResponse);

        Product saved = ProductMapper.dtoToProduct(productDTO, mockRate);
        saved.setId(10L);

        when(productRepository.save(any())).thenReturn(saved);

        ProductDTO result = productService.addProduct(productDTO);

        assertEquals(productDTOName, result.getName());
    }

    @Test
    void addProduct_nameExists_throwsException() {
        when(productRepository.findByName(productDTOName)).thenReturn(List.of(new Product()));

        assertThrows(EntityExistsException.class, () -> productService.addProduct(productDTO));
    }

    @Test
    void updateProduct() {
        Long id = 1L;
        HnbResponse mockResponse = new HnbResponse();
        mockResponse.setMiddleRate(mockRate);

        when(productRepository.existsById(id)).thenReturn(true);
        when(productRepository.findById(id)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findByName(productDTOName)).thenReturn(List.of());
        when(productRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(fetchHnbData.fetchData()).thenReturn(mockResponse);

        ProductDTO result = productService.updateProduct(id, productDTO);

        assertEquals(productDTOName, result.getName());
    }

    @Test
    void updateProduct_notFound() {
        when(productRepository.existsById(404L)).thenReturn(false);

        assertThrows(FetchNotFoundException.class, () -> productService.updateProduct(404L, productDTO));
    }

    @Test
    void deleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_notFound() {
        when(productRepository.existsById(5L)).thenReturn(false);

        assertThrows(FetchNotFoundException.class, () -> productService.deleteProduct(5L));
    }

    private Product createProduct(String name, BigDecimal priceEur, BigDecimal middleUsdRate, Boolean isAvailable) {
        return new Product(name, priceEur, middleUsdRate, isAvailable);
    }

    private ProductDTO createProductDTO(String name, BigDecimal priceEur, Boolean isAvailable) {
        return new ProductDTO(name, priceEur, isAvailable);
    }
}