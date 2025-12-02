package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.model.Product;
import com.simplegoods.simplegoods.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(BigDecimal.valueOf(10.0));

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(BigDecimal.valueOf(20.0));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals(product1.getId(), foundProduct.getId());
        assertEquals(product1.getName(), foundProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenNotExists_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct() {
        when(productRepository.save(product1)).thenReturn(product1);

        Product savedProduct = productService.createProduct(product1);

        assertNotNull(savedProduct);
        assertEquals(product1.getName(), savedProduct.getName());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void updateProduct_WhenExists_ShouldReturnUpdatedProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(product1)).thenReturn(product1);

        Product updatedProduct = productService.updateProduct(1L, product1);

        assertNotNull(updatedProduct);
        assertEquals(1L, updatedProduct.getId());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void updateProduct_WhenNotExists_ShouldThrowException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(1L, product1);
        });

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(0)).save(product1);
    }

    @Test
    void deleteProduct_WhenExists_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldThrowException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(1L);
        });

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(0)).deleteById(1L);
    }
}
