package com.simplegoods.simplegoods.controller;

import com.simplegoods.simplegoods.model.Product;
import com.simplegoods.simplegoods.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(Product product) {
        return productService.createProduct(product);
    }

    @PostMapping("/{id}")
    public Product updateProduct(Long id, Product product) {
        return productService.updateProduct(id, product);
    }

    @PostMapping("/{id}/delete")
    public void deleteProduct(@PathVariable("id")  Long id) {
        productService.deleteProduct(id);
    }

}
