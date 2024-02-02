package com.example.movieapp.service;

import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.User;
import com.example.movieapp.entity.Wishlist;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.dto.WishlistDto;
import com.example.movieapp.model.request.AddWishlistRequest;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.repository.WishlistRepository;
import com.example.movieapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final FilmRepository filmRepository;

    // create method get list WishListPublic by userId
    public List<WishlistDto> getWishlistsOfCurrentUser() {
        User user = SecurityUtils.getCurrentUserLogin();
        return wishlistRepository.findByUser_Id(user.getId());
    }

    public Wishlist addToWishlist(AddWishlistRequest request) {
        User user = SecurityUtils.getCurrentUserLogin();

        // Kiểm tra xem filmId có tồn tại trong database không
        // Nếu không tồn tại thì throw exception
        Film film = filmRepository.findById(request.getFilmId())
                .orElseThrow(() -> new ResourceNotFoundException("Phim không tồn tại"));

        // Kiểm tra xem phim đã có trong danh sách yêu thích của user chưa
        // Nếu có rồi thì throw exception
        // Nếu chưa thì thêm vào danh sách yêu thích
        if (wishlistRepository.findByUser_IdAndFilm_Id(user.getId(), request.getFilmId()).isPresent()) {
            throw new BadRequestException("Phim đã có trong danh sách yêu thích");
        }

        Wishlist wishlist = Wishlist.builder()
                .film(film)
                .user(user)
                .build();

        return wishlistRepository.save(wishlist);
    }

    public void deleteFromWishList(Integer filmId) {
        User user = SecurityUtils.getCurrentUserLogin();

        // Kiểm tra xem phim có tồn tại trong danh sách yêu thích của user không
        // Nếu không tồn tại thì throw exception
        // Nếu tồn tại thì xóa phim đó khỏi danh sách yêu thích
        Wishlist wishlist = wishlistRepository.findByUser_IdAndFilm_Id(user.getId(), filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Phim không tồn tại trong danh sách yêu thích"));

        wishlistRepository.delete(wishlist);
    }

    public boolean checkFavorite(Integer filmId) {
        User user = SecurityUtils.getCurrentUserLogin();
        if (user == null) {
            return false;
        }
        return wishlistRepository.existsByUser_IdAndFilm_Id(user.getId(), filmId);
    }
}
