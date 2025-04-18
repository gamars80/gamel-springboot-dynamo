package com.example.gamel.contoller;

import com.example.gamel.dto.PriceUpdateRequest;
import com.example.gamel.dto.ProductDto;
import com.example.gamel.entity.Product;
import com.example.gamel.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{categoryId}/products")
    public Page<ProductDto> getProducts(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        return productService.getProductsByCategory(categoryId, page, size, sort);
    }

    @PutMapping("/{productId}/price")
    public ResponseEntity<ProductDto> updateProductPrice(@PathVariable Long productId,
                                                         @RequestBody PriceUpdateRequest request) {
        ProductDto updatedProduct = productService.updateProductPrice(productId, request.getPrice());
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ProductDto> getProductDetail(@PathVariable Long id) {
        ProductDto productDto = productService.getProductDetail(id);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
}