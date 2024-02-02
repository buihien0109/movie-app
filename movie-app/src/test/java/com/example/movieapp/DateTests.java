package com.example.movieapp;

import com.example.movieapp.entity.*;
import com.example.movieapp.model.dto.ReviewDto;
import com.example.movieapp.repository.*;
import com.example.movieapp.utils.StringUtils;
import com.github.slugify.Slugify;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
public class DateTests {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;


    @Test
    void rd_date_blog() {
        List<Blog> blogs = blogRepository.findAll();
        Date start = new Calendar.Builder().setDate(2023, 11, 1).build().getTime();
        Date end = new Date();

        for (Blog blog : blogs) {
            Date date = randomDateBetweenTwoDates(start, end);
            blog.setCreatedAt(date);
            blog.setUpdatedAt(date);
            blog.setPublishedAt(date);
            blogRepository.save(blog);
        }
    }

    @Test
    void rd_date_film() {
        List<Film> films = filmRepository.findAll();
        Date start = new Calendar.Builder().setDate(2023, 11, 1).build().getTime();
        Date end = new Date();

        for (Film film : films) {
            Date date = randomDateBetweenTwoDates(start, end);
            film.setCreatedAt(date);
            film.setUpdatedAt(date);
            film.setPublishedAt(date);
            filmRepository.save(film);
        }
    }

    @Test
    void update_rating_film() {
        List<Film> films = filmRepository.findAll();
        for (Film film : films) {
            // Get all review of film
            List<ReviewDto> reviews = reviewRepository.findByFilm_IdOrderByCreatedAtDesc(film.getId());

            // Tính toán rating trung bình và làm tròn 1 chữ số thập phân
            double rating = 0;
            for (ReviewDto review : reviews) {
                rating += review.getRating();
            }
            if (!reviews.isEmpty()) {
                rating = rating / reviews.size();
                rating = Math.round(rating * 10) / 10.0;
            }

            film.setRating(rating);
            filmRepository.save(film);
        }
    }

    @Test
    void update_phone_user() {
        // Random ds số điện thoại Việt Nam bao gồm 10 số
        Random random = new Random();
        List<String> phones = List.of(
                "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
                "090", "093", "070", "079", "077", "076", "078", "089", "088", "091", "094", "083",
                "085", "081", "082", "092", "056", "058", "099"
        );

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            StringBuilder phone = new StringBuilder(phones.get(random.nextInt(phones.size())));
            for (int i = 0; i < 7; i++) {
                phone.append(random.nextInt(10));
            }
            user.setPhone(phone.toString());
            userRepository.save(user);
        }
    }

    @Test
    void update_info_user() {
        // Random ds họ người dùng theo tên gọi Việt Nam
        Random random = new Random();

        // Tạo slugify object không có dấu -
        Slugify slugify = Slugify.builder().build();

        List<String> listHo = List.of(
                "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng",
                "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Đào", "Đinh", "Lâm", "Phùng", "Mai",
                "Tô", "Trịnh", "Đoàn", "Tăng", "Bành", "Hà", "Thái", "Tạ", "Tăng", "Thi"
        );

        // Random ds tên đệm người dùng theo tên gọi Việt Nam
        List<String> listTenDem = List.of(
                "Văn", "Thị", "Hồng", "Hải", "Hà", "Hưng", "Hùng", "Hạnh", "Hạ", "Thanh");

        // Random ds tên người dùng theo tên gọi Việt Nam (30 tên phổ biến từ A -> Z) (ít vần H)
        List<String> listTen = List.of(
                "An", "Bình", "Cường", "Dũng", "Đức", "Giang", "Hải", "Hào", "Hùng", "Hưng", "Minh", "Nam", "Nghĩa", "Phong", "Phúc", "Quân", "Quang", "Quốc", "Sơn", "Thắng", "Thành", "Thiên", "Thịnh", "Thuận", "Tiến", "Trung", "Tuấn", "Vinh", "Vũ", "Xuân"
        );

        // Random ds số điện thoại Việt Nam bao gồm 10 số
        List<String> phones = List.of(
                "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
                "090", "093", "070", "079", "077", "076", "078", "089", "088", "091", "094", "083",
                "085", "081", "082", "092", "056", "058", "099"
        );

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            String ho = listHo.get(random.nextInt(listHo.size()));
            String tenDem = listTenDem.get(random.nextInt(listTenDem.size()));
            String ten = listTen.get(random.nextInt(listTen.size()));

            String fullName = ho + " " + tenDem + " " + ten;
            String email = slugify.slugify(fullName.toLowerCase()).replaceAll("-", "") + "@gmail.com";

            user.setName(ho + " " + tenDem + " " + ten);
            user.setAvatar(StringUtils.generateLinkImage(ten));
            user.setEmail(email);

            StringBuilder phone = new StringBuilder(phones.get(random.nextInt(phones.size())));
            for (int i = 0; i < 7; i++) {
                phone.append(random.nextInt(10));
            }
            user.setPhone(phone.toString());

            userRepository.save(user);
        }
    }

    @Test
    void rd_trailer_film() {
        Random random = new Random();
        List<Film> films = filmRepository.findAll();
        List<String> trailers = List.of(
                "https://www.youtube.com/embed/Y0ZsLudtfjI",
                "https://www.youtube.com/embed/qQlr9-rF32A",
                "https://www.youtube.com/embed/g8zxiB5Qhsc",
                "https://www.youtube.com/embed/wS_qbDztgVY",
                "https://www.youtube.com/embed/aDyQxtg0V2w&t=1s",
                "https://www.youtube.com/embed/G0S6S9Sks70",
                "https://www.youtube.com/embed/E2payjgyzw0",
                "https://www.youtube.com/embed/leZABQZcyh0",
                "https://www.youtube.com/embed/ASrbrFuagHQ",
                "https://www.youtube.com/embed/6hjlyknlTaw",
                "https://www.youtube.com/embed/rze8QYwWGMs&t=1s",
                "https://www.youtube.com/embed/XFs7UdM2ByE",
                "https://www.youtube.com/embed/dwZztoxJFs8",
                "https://www.youtube.com/embed/ByAn8DF8Ykk&t=33s",
                "https://www.youtube.com/embed/sBU8ejvW6fM",
                "https://www.youtube.com/embed/lV1OOlGwExM&t=1s",
                "https://www.youtube.com/embed/e0CJbdZtzI0",
                "https://www.youtube.com/embed/Vv1nfDIJ9kU",
                "https://www.youtube.com/embed/zvwDen1Wrx8",
                "https://www.youtube.com/embed/NQ_HvTBaFoo&t=1s",
                "https://www.youtube.com/embed/L8Blo2a00k8",
                "https://www.youtube.com/embed/nS12Fbtgr5A",
                "https://www.youtube.com/embed/itnqEauWQZM",
                "https://www.youtube.com/embed/rthWUciXTyk",
                "https://www.youtube.com/embed/zlgdoF4-8Ek",
                "https://www.youtube.com/embed/gBHde1DVp5c",
                "https://www.youtube.com/embed/M7vzZTPpdzs",
                "https://www.youtube.com/embed/hhnFo21ZlYc",
                "https://www.youtube.com/embed/SzINZZ6iqxY",
                "https://www.youtube.com/embed/WXpBN_31-Cw",
                "https://www.youtube.com/embed/UZNmDKJz41g",
                "https://www.youtube.com/embed/Rs06z0erbLc"
        );
        for (Film film : films) {
            String trailer = trailers.get(random.nextInt(trailers.size()));
            film.setTrailerUrl(trailer);
            filmRepository.save(film);
        }
    }

    @Test
    void rd_country_film() {
        List<Film> films = filmRepository.findAll();
        List<Country> countries = countryRepository.findAll();
        Random random = new Random();
        for (Film film : films) {
            film.setCountry(countries.get(random.nextInt(countries.size())));
            filmRepository.save(film);
        }
    }

    // write method to random date between 2 date
    private Date randomDateBetweenTwoDates(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);
        return new Date(randomMillisSinceEpoch);
    }
}
