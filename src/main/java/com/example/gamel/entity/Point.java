package com.example.gamel.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long reviewId;

    @Column(nullable = false)
    private int points;


    public static Point create(Long userId, Long productId, Long reviewId, int points) {
        Point point = new Point();
        point.userId = userId;
        point.productId = productId;
        point.reviewId = reviewId;
        point.points = points;
        return point;
    }
}