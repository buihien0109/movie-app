package com.example.movieapp.controller;

import com.example.movieapp.entity.*;
import com.example.movieapp.model.enums.FilmType;
import com.example.movieapp.security.SecurityUtils;
import com.example.movieapp.service.WatchHistoryService;
import com.example.movieapp.service.WebService;
import com.example.movieapp.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebController {
    private final WebService webService;
    private final WishlistService wishlistService;
    private final WatchHistoryService watchHistoryService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Film> phimHotList = webService.getPhimHot(8);
        Page<Film> phimLeList = webService.getFilmsByType(FilmType.PHIM_LE, true, 1, 6);
        Page<Film> phimBoList = webService.getFilmsByType(FilmType.PHIM_BO, true, 1, 6);
        Page<Film> phimChieuRapList = webService.getFilmsByType(FilmType.PHIM_CHIEU_RAP, true, 1, 6);
        Page<Blog> blogList = webService.getAllBlogs(1, 4);

        model.addAttribute("phimHotList", phimHotList);
        model.addAttribute("phimLeList", phimLeList.getContent());
        model.addAttribute("phimBoList", phimBoList.getContent());
        model.addAttribute("phimChieuRapList", phimChieuRapList.getContent());
        model.addAttribute("blogList", blogList.getContent());
        return "web/index";
    }

    @GetMapping("/phim-le")
    public String getPhimLePage(Model model,
                                @RequestParam(required = false, defaultValue = "1") Integer page,
                                @RequestParam(required = false, defaultValue = "18") Integer limit) {
        Page<Film> pageData = webService.getFilmsByType(FilmType.PHIM_LE, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/phim-le";
    }

    @GetMapping("/phim-bo")
    public String getPhimMoiPage(Model model,
                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "18") Integer limit) {
        Page<Film> pageData = webService.getFilmsByType(FilmType.PHIM_BO, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/phim-bo";
    }

    @GetMapping("/phim-chieu-rap")
    public String getPhimChieuRapPage(Model model,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "18") Integer limit) {
        Page<Film> pageData = webService.getFilmsByType(FilmType.PHIM_CHIEU_RAP, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/phim-chieu-rap";
    }

    @GetMapping("/phim/{id}/{slug}")
    public String getPhimLeDetailPage(Model model,
                                      @PathVariable Integer id,
                                      @PathVariable String slug) {
        Film film = webService.findFilmByIdAndSlug(id, slug);
        List<Episode> episodeList = webService.getEpisodesByFilmId(id);
        List<Film> relateFilmList = webService.getRelateFilms(id, 6);
        List<Review> reviewList = webService.getReviewsOfFilm(id);
        User user = SecurityUtils.getCurrentUserLogin();
        boolean isFavorite = wishlistService.checkFavorite(id);

        model.addAttribute("film", film);
        model.addAttribute("episodeList", episodeList);
        model.addAttribute("relateFilmList", relateFilmList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("currentUser", user);
        model.addAttribute("isFavorite", isFavorite);
        return "web/chi-tiet-phim";
    }

    @GetMapping("/xem-phim/{id}/{slug}")
    public String getXemPhimPage(Model model,
                                 @PathVariable Integer id,
                                 @PathVariable String slug,
                                 @RequestParam(required = false, defaultValue = "1") String tap) {
        Film film = webService.findFilmByIdAndSlug(id, slug);
        List<Film> relateFilmList = webService.getRelateFilms(id, 6);
        List<Episode> episodeList = webService.getEpisodesByFilmId(id);
        List<Review> reviewList = webService.getReviewsOfFilm(id);
        Episode currentEpisode = webService.getEpisodeByDisplayOrder(id, true, tap);
        User user = SecurityUtils.getCurrentUserLogin();

        WatchHistory watchHistory = watchHistoryService.getWatchHistory(id, currentEpisode.getId());
        boolean isFavorite = wishlistService.checkFavorite(id);


        model.addAttribute("film", film);
        model.addAttribute("relateFilmList", relateFilmList);
        model.addAttribute("episodeList", episodeList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("currentEpisode", currentEpisode);
        model.addAttribute("tap", tap);
        model.addAttribute("currentUser", user);
        model.addAttribute("watchHistory", watchHistory);
        model.addAttribute("isFavorite", isFavorite);
        return "web/xem-phim";
    }

    @GetMapping("/tin-tuc")
    public String getBlogsPage(Model model,
                               @RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Page<Blog> pageData = webService.getAllBlogs(page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/tin-tuc";
    }

    @GetMapping("/tin-tuc/{id}/{slug}")
    public String getBlogDetailPage(Model model,
                                    @PathVariable Integer id,
                                    @PathVariable String slug) {
        Blog blog = webService.getBlogByIdAndSlug(id, slug);
        model.addAttribute("blog", blog);
        return "web/chi-tiet-tin-tuc";
    }

    @GetMapping("/phim-yeu-thich")
    public String getWishlistPage(Model model) {
        List<Wishlist> wishlistList = wishlistService.getWishlistsOfCurrentUser();
        model.addAttribute("wishlistList", wishlistList);
        return "web/phim-yeu-thich";
    }

    @GetMapping("/lich-su-xem-phim")
    public String getWatchHistoryPage(Model model) {
        List<WatchHistory> watchHistoryList = watchHistoryService.getWatchHistoriesOfCurrentUser();
        model.addAttribute("watchHistoryList", watchHistoryList);
        return "web/lich-su-xem-phim";
    }

    @GetMapping("/dang-nhap")
    public String getLoginPage() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/";
        }
        return "web/login";
    }

    @GetMapping("/dang-ky")
    public String getRegisterPage() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/";
        }
        return "web/register";
    }
}

