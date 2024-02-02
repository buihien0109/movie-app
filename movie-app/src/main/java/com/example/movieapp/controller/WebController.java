package com.example.movieapp.controller;

import com.example.movieapp.entity.*;
import com.example.movieapp.model.dto.*;
import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.FilmType;
import com.example.movieapp.security.SecurityUtils;
import com.example.movieapp.service.*;
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
    private final OrderService orderService;
    private final FilmService filmService;
    private final AuthService authService;
    private final BlogService blogService;
    private final GenreService genreService;
    private final CountryService countryService;

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<FilmDto> phimHotList = webService.getPhimHot(8);
        Page<FilmDto> phimLeList = webService.getFilmsByType(FilmType.PHIM_LE, FilmAccessType.FREE, true, 1, 6);
        Page<FilmDto> phimBoList = webService.getFilmsByType(FilmType.PHIM_BO, FilmAccessType.FREE, true, 1, 6);
        Page<FilmDto> phimChieuRapList = webService.getFilmsByType(FilmType.PHIM_CHIEU_RAP, FilmAccessType.FREE, true, 1, 6);
        Page<BlogDto> blogList = webService.getAllBlogs(1, 4);

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
        Page<FilmDto> pageData = webService.getFilmsByType(FilmType.PHIM_LE, FilmAccessType.FREE, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/film/phim-le";
    }

    @GetMapping("/phim-bo")
    public String getPhimMoiPage(Model model,
                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "18") Integer limit) {
        Page<FilmDto> pageData = webService.getFilmsByType(FilmType.PHIM_BO, FilmAccessType.FREE, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/film/phim-bo";
    }

    @GetMapping("/phim-chieu-rap")
    public String getPhimChieuRapPage(Model model,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "18") Integer limit) {
        Page<FilmDto> pageData = webService.getFilmsByType(FilmType.PHIM_CHIEU_RAP, FilmAccessType.FREE, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/film/phim-chieu-rap";
    }

    @GetMapping("/the-loai/{slug}")
    public String getTheLoaiPage(Model model,
                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "18") Integer limit,
                                 @PathVariable String slug) {
        Genre genre = genreService.getGenreBySlug(slug);
        Page<FilmDto> pageData = filmService.getFilmsOfGenre(slug, FilmAccessType.FREE, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        model.addAttribute("genre", genre);
        return "web/film/the-loai";
    }

    @GetMapping("/quoc-gia/{slug}")
    public String getQuocGiaPage(Model model,
                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "18") Integer limit,
                                 @PathVariable String slug) {
        Country country = countryService.getCountryBySlug(slug);
        Page<FilmDto> pageData = filmService.getFilmsOfCountry(slug, FilmAccessType.FREE, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        model.addAttribute("country", country);
        return "web/film/quoc-gia";
    }

    @GetMapping("/cua-hang")
    public String getMuaPhimPage(Model model,
                                 @RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "18") Integer limit) {
        Page<FilmDto> pageData = webService.getFilmsByAccessType(FilmAccessType.PAID, true, page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/store/cua-hang";
    }

    @GetMapping("/cua-hang/phim/{id}/{slug}")
    public String getMuaPhimPage(Model model,
                                 @PathVariable Integer id,
                                 @PathVariable String slug) {
        Film film = webService.findFilmByIdAndSlug(id, slug, FilmAccessType.PAID);
        List<EpisodeDto> episodeList = webService.getEpisodesByFilmId(id);
        List<Film> relateFilmList = webService.getRelateProFilms(id, 6);
        List<ReviewDto> reviewList = webService.getReviewsOfFilm(id);
        User user = SecurityUtils.getCurrentUserLogin();
        boolean isFavorite = wishlistService.checkFavorite(id);
        boolean isBuyed = orderService.checkUserBoughtFilm(id);

        model.addAttribute("film", film);
        model.addAttribute("episodeList", episodeList);
        model.addAttribute("relateFilmList", relateFilmList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("currentUser", user);
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("isBuyed", isBuyed);
        return "web/store/chi-tiet-phim-mua";
    }

    @GetMapping("/phim/{id}/{slug}")
    public String getPhimLeDetailPage(Model model,
                                      @PathVariable Integer id,
                                      @PathVariable String slug) {
        Film film = webService.findFilmByIdAndSlug(id, slug, FilmAccessType.FREE);
        List<EpisodeDto> episodeList = webService.getEpisodesByFilmId(id);
        List<FilmDto> relateFilmList = webService.getRelateFilms(id, 6);
        List<ReviewDto> reviewList = webService.getReviewsOfFilm(id);
        User user = SecurityUtils.getCurrentUserLogin();
        boolean isFavorite = wishlistService.checkFavorite(id);

        model.addAttribute("film", film);
        model.addAttribute("episodeList", episodeList);
        model.addAttribute("relateFilmList", relateFilmList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("currentUser", user);
        model.addAttribute("isFavorite", isFavorite);
        return "web/film/chi-tiet-phim";
    }

    @GetMapping("/xem-phim/{id}/{slug}")
    public String getXemPhimPage(Model model,
                                 @PathVariable Integer id,
                                 @PathVariable String slug,
                                 @RequestParam(required = false, defaultValue = "1") String tap) {
        Film film = webService.findFilmByIdAndSlug(id, slug, FilmAccessType.FREE);
        List<FilmDto> relateFilmList = webService.getRelateFilms(id, 6);
        List<EpisodeDto> episodeList = webService.getEpisodesByFilmId(id);
        List<ReviewDto> reviewList = webService.getReviewsOfFilm(id);
        EpisodeDto currentEpisode = webService.getEpisodeByDisplayOrder(id, true, tap);
        User user = SecurityUtils.getCurrentUserLogin();
        boolean isFavorite = wishlistService.checkFavorite(id);

        if(currentEpisode != null) {
            WatchHistoryDto watchHistory = watchHistoryService.getWatchHistory(id, currentEpisode.getId());
            model.addAttribute("watchHistory", watchHistory);
        }

        model.addAttribute("film", film);
        model.addAttribute("relateFilmList", relateFilmList);
        model.addAttribute("episodeList", episodeList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("currentEpisode", currentEpisode);
        model.addAttribute("tap", tap);
        model.addAttribute("currentUser", user);
        model.addAttribute("isFavorite", isFavorite);

        return "web/film/xem-phim";
    }

    @GetMapping("/store/xem-phim/{id}/{slug}")
    public String getXemPhimMuaPage(Model model,
                                    @PathVariable Integer id,
                                    @PathVariable String slug,
                                    @RequestParam(required = false, defaultValue = "1") String tap) {
        Film film = webService.findFilmByIdAndSlug(id, slug, FilmAccessType.PAID);
        List<Film> relateFilmList = webService.getRelateProFilms(id, 6);
        List<EpisodeDto> episodeList = webService.getEpisodesByFilmId(id);
        List<ReviewDto> reviewList = webService.getReviewsOfFilm(id);
        EpisodeDto currentEpisode = webService.getEpisodeByDisplayOrder(id, true, tap);
        User user = SecurityUtils.getCurrentUserLogin();
        boolean isFavorite = wishlistService.checkFavorite(id);
        boolean isBuyed = orderService.checkUserBoughtFilm(id);

        if(currentEpisode != null) {
            WatchHistoryDto watchHistory = watchHistoryService.getWatchHistory(id, currentEpisode.getId());
            model.addAttribute("watchHistory", watchHistory);
        }

        model.addAttribute("film", film);
        model.addAttribute("relateFilmList", relateFilmList);
        model.addAttribute("episodeList", episodeList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("currentEpisode", currentEpisode);
        model.addAttribute("tap", tap);
        model.addAttribute("currentUser", user);
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("isBuyed", isBuyed);
        return "web/store/xem-phim-mua";
    }

    @GetMapping("/tin-tuc")
    public String getBlogsPage(Model model,
                               @RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Page<BlogDto> pageData = webService.getAllBlogs(page, limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageData", pageData);
        return "web/tin-tuc/tin-tuc";
    }

    @GetMapping("/tin-tuc/{id}/{slug}")
    public String getBlogDetailPage(Model model,
                                    @PathVariable Integer id,
                                    @PathVariable String slug) {
        BlogDetailDto blog = webService.getBlogByIdAndSlug(id, slug);
        Page<BlogDto> blogPage = blogService.getNewestBlogs(0, 6, id);
        model.addAttribute("blog", blog);
        model.addAttribute("newBlogList", blogPage.getContent());
        return "web/tin-tuc/chi-tiet-tin-tuc";
    }

    @GetMapping("/phim-yeu-thich")
    public String getWishlistPage(Model model) {
        List<WishlistDto> wishlistList = wishlistService.getWishlistsOfCurrentUser();
        model.addAttribute("wishlistList", wishlistList);
        return "web/user/phim-yeu-thich";
    }

    @GetMapping("/lich-su-xem-phim")
    public String getWatchHistoryPage(Model model) {
        List<WatchHistoryDto> watchHistoryList = watchHistoryService.getWatchHistoriesOfCurrentUser();
        model.addAttribute("watchHistoryList", watchHistoryList);
        return "web/user/lich-su-xem-phim";
    }

    @GetMapping("/thong-tin-ca-nhan")
    public String getProfilePage(Model model) {
        User user = SecurityUtils.getCurrentUserLogin();
        model.addAttribute("user", user);
        return "web/user/thong-tin-ca-nhan";
    }

    @GetMapping("/lich-su-giao-dich")
    public String getOrderHistoryPage(Model model) {
        List<OrderDto> orderList = orderService.getOrdersOfCurrentUser();
        model.addAttribute("orderList", orderList);
        return "web/user/lich-su-giao-dich";
    }

    @GetMapping("/danh-sach-phim-mua")
    public String getPhimBuyedPage(Model model) {
        List<FilmDto> filmList = filmService.getFilmsBuyedOfCurrentUser();
        model.addAttribute("filmList", filmList);
        return "web/user/danh-sach-phim-mua";
    }

    @GetMapping("/xac-thuc-tai-khoan")
    public String confirm(@RequestParam String token, Model model) {
        model.addAttribute("data", authService.confirmEmail(token));
        return "web/auth/xac-thuc-tai-khoan";
    }

    @GetMapping("/quen-mat-khau")
    public String getForgotPassword() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/";
        }
        return "web/auth/quen-mat-khau";
    }

    @GetMapping("/dat-lai-mat-khau")
    public String resetPassword(@RequestParam String token, Model model) {
        model.addAttribute("data", authService.confirmResetPassword(token));
        return "web/auth/dat-lai-mat-khau";
    }

    @GetMapping("/dang-nhap")
    public String getLoginPage() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/";
        }
        return "web/auth/login";
    }

    @GetMapping("/dang-ky")
    public String getRegisterPage() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/";
        }
        return "web/auth/register";
    }
}

