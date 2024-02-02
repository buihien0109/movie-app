package com.example.movieapp.rest;

import com.example.movieapp.entity.Wishlist;
import com.example.movieapp.model.request.AddWishlistRequest;
import com.example.movieapp.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistResource {
    private final WishlistService wishListService;

    @PostMapping()
    public ResponseEntity<?> addToWishList(@Valid @RequestBody AddWishlistRequest request) {
        Wishlist wishList = wishListService.addToWishlist(request);
        return ResponseEntity.ok(wishList);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteWishList(@RequestParam Integer filmId) {
        wishListService.deleteFromWishList(filmId);
        return ResponseEntity.noContent().build();
    }
}
