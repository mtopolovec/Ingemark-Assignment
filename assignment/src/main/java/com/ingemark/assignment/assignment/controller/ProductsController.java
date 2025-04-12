package com.ingemark.assignment.assignment.controller;

import com.ingemark.assignment.assignment.dto.ApiResponseMsg;
import com.ingemark.assignment.assignment.dto.ProductDTO;
import com.ingemark.assignment.assignment.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductsController {
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDTO> allProducts(
            @RequestParam(name = "status", required = false)
            @Pattern(regexp = "^(?i)(true|false)$", message = "Status must be 'true' or 'false' (case insensitive)")
            String status
    ) {
        if (status != null && !status.isEmpty()) {
            log.debug("Getting all products with status {}.", status);
            return productService.getAllProductsWithStatus(Boolean.valueOf(status.toLowerCase()));
        }
        log.debug("Getting all products.");
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") final Long id) {
        log.debug("Getting product with id of {}.", id);
        ProductDTO product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody final ProductDTO item) {
        log.debug("Adding new product");
        ProductDTO product = productService.addProduct(item);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") final Long id,
                                                    @Valid @RequestBody final ProductDTO item) {
        log.debug("Updating product with id of {}.", id);
        ProductDTO product = productService.updateProduct(id, item);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseMsg> deleteProduct(@PathVariable("id") final Long id) {
        log.debug("Deleted product with id of {}.", id);
        productService.deleteProduct(id);
        String message = "Successfully deleted product with id: " + id;
        return new ResponseEntity<>(new ApiResponseMsg(200, message, null), HttpStatus.OK);
    }
}
