package com.example.gamel.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceUpdateRequest {
    private int price;
}