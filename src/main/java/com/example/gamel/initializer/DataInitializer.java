package com.example.gamel.initializer;

import com.example.gamel.entity.Product;
import com.example.gamel.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            for (int i = 1; i <= 100; i++) {

                productRepository.save(Product.create(Long.valueOf(1), "Product " + i, "Description for product " + i,
                        1000 + i));
            }
            System.out.println("초기 데이터 100건이 삽입되었습니다.");
        }
    }
}