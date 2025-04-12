package com.ingemark.assignment.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingemark.assignment.assignment.dto.ProductDTO;
import com.ingemark.assignment.assignment.model.Product;
import com.ingemark.assignment.assignment.repository.ProductRepository;
import com.ingemark.assignment.assignment.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    String firstName = "Test first name";
    String secondName = "Test second name";
    String thirdName = "Test third name";
    Product firstProduct;
    Product secondProduct;
    Product thirdProduct;
    Long id;

    @BeforeEach
    void setUp() {
        BigDecimal mockRate = new BigDecimal("1.1");

        firstProduct = createProduct(firstName, new BigDecimal("19.99"), mockRate, true);
        secondProduct = createProduct(secondName, new BigDecimal("29.99"), mockRate, false);
        thirdProduct = createProduct(thirdName, new BigDecimal("199.99"), mockRate, true);

        List<Product> savedProducts = productRepository.saveAllAndFlush(List.of(firstProduct, secondProduct, thirdProduct));
        id = savedProducts.getFirst().getId();
    }

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldReturnAllProducts() throws Exception {

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value(firstName))
                .andExpect(jsonPath("$[1].name").value(secondName))
                .andExpect(jsonPath("$[2].name").value(thirdName));
    }

    @Test
    void shouldAddProduct() throws Exception {
        ProductDTO dto = createProductDTO("TestProduct");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProduct"))
                .andExpect(jsonPath("$.priceEur").value(9.99))
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    void shouldReturn400WhenInvalidProductData() throws Exception {
        ProductDTO invalidProductDTO = new ProductDTO("", new BigDecimal("199.99"), true);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProductDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("One or more fields failed validation."))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", containsInAnyOrder(
                        "name: size must be between 3 and 30",
                        "name: Name cannot be blank."
                )));
    }

    @Test
    void shouldGetProductById() throws Exception {
        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(firstName));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(get("/api/products/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("There is no product with this Id: " + nonExistentId + "."));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductDTO updateDto = createProductDTO("UpdatedName");

        mockMvc.perform(put("/api/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Successfully deleted product")));
    }

    private Product createProduct(String name, BigDecimal priceEur, BigDecimal middleUsdRate, Boolean isAvailable) {
        return new Product(name, priceEur, middleUsdRate, isAvailable);
    }

    private ProductDTO createProductDTO(String name) {
        return new ProductDTO(name, BigDecimal.valueOf(9.99), true);
    }
}