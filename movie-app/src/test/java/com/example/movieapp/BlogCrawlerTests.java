package com.example.movieapp;

import com.example.movieapp.utils.crawl.BlogCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BlogCrawlerTests {
    @Autowired
    private BlogCrawler blogCrawler;

    @Test
    public void testCrawlBlogPost() {
        blogCrawler.crawlBlogPost("https://momo.vn/cinema/blog/nhung-bo-phim-hay-nhat-cua-chung-tu-don-482");
    }

    @Test
    public void testAllCrawlBlogPost() {
        blogCrawler.crawlAllBlogPost();
    }
}
