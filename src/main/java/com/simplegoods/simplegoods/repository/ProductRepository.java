package com.simplegoods.simplegoods.repository;

import com.simplegoods.simplegoods.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
