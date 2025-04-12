package com.ingemark.assignment.assignment.repository;

import com.ingemark.assignment.assignment.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);
    List<Product> findByIsAvailable(Boolean isAvailable);
}
