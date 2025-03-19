package com.example.gamel.dto;


import com.example.gamel.entity.dynamo.ProductReview;

import java.util.List;
import java.util.Map;

public class PaginatedProductReview {

    private List<ProductReview> reviews;
    private Map<String, String> lastEvaluatedKey;

    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }

    public Map<String, String> getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

    public void setLastEvaluatedKey(Map<String, String> lastEvaluatedKey) {
        this.lastEvaluatedKey = lastEvaluatedKey;
    }
}