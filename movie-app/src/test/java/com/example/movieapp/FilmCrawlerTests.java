package com.example.movieapp;

import com.example.movieapp.utils.crawl.FilmCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FilmCrawlerTests {
    @Autowired
    private FilmCrawler filmCrawler;

    @Test
    void test_crawlFilm() {
        filmCrawler.crawlFilm("https://momo.vn/cinema/antman-and-the-wasp-quantumania-851");
    }

    @Test
    void test_crawlAllFilm() {
        filmCrawler.crawlAllFilm();
    }
}
