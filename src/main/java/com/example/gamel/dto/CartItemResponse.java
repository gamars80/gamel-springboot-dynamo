package com.example.gamel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    String productName;
    String thumbnailUrl;
    Integer price;
    Integer quantity;
}
