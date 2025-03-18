package com.example.gamel.dto;

import com.example.gamel.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private int price;
    private Long categoryId;

    private List<ProductReviewDto> reviews;

    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategoryId());
        return dto;
    }
}