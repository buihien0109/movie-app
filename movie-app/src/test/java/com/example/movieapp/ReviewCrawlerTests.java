package com.example.movieapp;

import com.example.movieapp.utils.crawl.ReviewCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReviewCrawlerTests {
    @Autowired
    private ReviewCrawler reviewCrawler;
}
