package com.example.gamel.contoller;

import com.example.gamel.dto.PaginatedProductReview;
import com.example.gamel.entity.dynamo.ProductReview;
import com.example.gamel.service.ProductReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;


    @PostMapping
    public ResponseEntity<Map<String, Object>> addReview(@RequestBody ProductReview review){
        // reviewId를 Long 타입으로 생성 (예: 현재 시간 사용)
        review.setReviewId(System.currentTimeMillis());
        review.setCreatedAt(java.time.Instant.now().toString());

        productReviewService.addReview(review);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Review added successfully");
        response.put("reviewId", review.getReviewId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PaginatedProductReview> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(required = false) Map<String, String> lastEvaluatedKeyParam) {

        // 만약 lastEvaluatedKeyParam이 전달된다면, 클라이언트에서 JSON 파싱을 통해 Map<String, AttributeValue>로 변환해야 합니다.
        // 여기서는 단순화를 위해 null로 처리
        Map<String, AttributeValue> exclusiveStartKey = null;

        PaginatedProductReview paginatedReviews = productReviewService.getPaginatedReviews(productId, limit, exclusiveStartKey);
        return ResponseEntity.ok(paginatedReviews);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductReview>> getReviewsByUser(@PathVariable Long userId){
        List<ProductReview> reviews = productReviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }


    @GetMapping("/{productId}/{reviewId}")
    public ResponseEntity<ProductReview> getReviewDetail(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        ProductReview review = productReviewService.getReview(productId, reviewId);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }


    @DeleteMapping("/{productId}/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId) {
        ProductReview review = productReviewService.getReview(productId, reviewId);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        productReviewService.deleteReview(productId, reviewId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Review deleted successfully");
        response.put("reviewId", reviewId);

        return ResponseEntity.ok(response);
    }
}