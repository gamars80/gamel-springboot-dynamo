package com.example.gamel.contoller;

import com.example.gamel.dto.CartItemRequest;
import com.example.gamel.dto.CartItemResponse;
import com.example.gamel.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    // 1. 장바구니에 상품 추가
    @PostMapping
    public ResponseEntity<Void> addItem(@RequestBody CartItemRequest request) {
        shoppingCartService.addItem(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // 2. 사용자 장바구니 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable String userId) {
        List<CartItemResponse> items = shoppingCartService.getCartItems(userId);
        return ResponseEntity.ok(items);
    }

    // 3. 장바구니 내 상품 수량 업데이트
    @PutMapping
    public ResponseEntity<Void> updateItem(@RequestBody CartItemRequest request) {
        shoppingCartService.updateItem(request.getUserId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // 4. 장바구니 내 상품 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteItem(@RequestParam String userId, @RequestParam String productId) {
        shoppingCartService.deleteItem(userId, productId);
        return ResponseEntity.ok().build();
    }

}