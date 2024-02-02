package com.example.movieapp.entity;

import com.example.movieapp.model.enums.OrderPaymentMethod;
import com.example.movieapp.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "film_id")
    Film film;

    Integer amount;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Enumerated(EnumType.STRING)
    OrderPaymentMethod paymentMethod;

    @Column(columnDefinition = "TEXT")
    String note;

    Date createdAt;
    Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
