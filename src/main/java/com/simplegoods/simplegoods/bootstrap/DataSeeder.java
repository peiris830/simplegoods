package com.simplegoods.simplegoods.bootstrap;

import com.simplegoods.simplegoods.model.Product;
import com.simplegoods.simplegoods.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String ... args) {
        if (productRepository.count() == 0) {                     // avoid duplicates
            List<Product> samples = List.of(
                    new Product(null, "Desk Lamp",
                            "Minimalist LED desk lamp",
                            new BigDecimal("29.99"), null),

                    new Product(null, "Wireless Mouse",
                            "Ergonomic 2.4 GHz mouse",
                            new BigDecimal("19.99"), null),

                    new Product(null, "Notebook",
                            "A5 dotted notebook (200 pages)",
                            new BigDecimal("6.50"), null)
            );
            productRepository.saveAll(samples);
            System.out.println("[DataSeeder] Inserted sample products.");
        }
    }

}
