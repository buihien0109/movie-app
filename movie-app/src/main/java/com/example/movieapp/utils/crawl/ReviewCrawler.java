package com.example.movieapp.utils.crawl;

import com.example.movieapp.entity.Film;
import com.example.movieapp.entity.Review;
import com.example.movieapp.entity.User;
import com.example.movieapp.repository.ReviewRepository;
import com.example.movieapp.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class ReviewCrawler {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private long initWaitTime = 1;

    public ReviewCrawler(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        WebDriverManager.chromedriver().setup();
    }

    public void crawlReviewsOfFilm(String url, Film film) {
        List<User> userList = userRepository.findAll();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(url);
            Thread.sleep(5000); // Wait for the dynamic content to load

            // Find review elements using Selenium
            List<WebElement> reviewElements = driver.findElements(By.cssSelector(".relative.w-full.py-5"));
            log.info("Review Elements: {}", reviewElements);
            List<Review> reviewList = parseReview(driver, reviewElements, userList, film);
            log.info("Review List");
            reviewList.forEach(review -> log.info("Review: {}", review));

            // Save to database
            reviewRepository.saveAll(reviewList);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error crawling review: {}", e.getMessage());
        } finally {
            driver.close();
        }
    }

    private List<Review> parseReview(WebDriver driver, List<WebElement> reviewElements, List<User> userList, Film film) {
        List<Review> reviewList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        for (WebElement reviewElement : reviewElements) {
            Integer rating = 10;
            if (isElementPresent(driver, reviewElement, ".h-5.w-5.text-yellow-400 + span")) {
                WebElement ratingElement = reviewElement.findElement(By.cssSelector(".h-5.w-5.text-yellow-400 + span"));
                log.info("Rating Element: {}", ratingElement);

                rating = Integer.valueOf(ratingElement.getText().split("/")[0]);
            }

            String createdAtString = reviewElement.findElement(By.cssSelector(".text-xs.text-gray-500")).getText();
            Date createdAt = null;
            try {
                createdAt = formatter.parse(createdAtString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            String comment = reviewElement.findElement(By.cssSelector(".whitespace-pre-wrap.break-words.text-md.leading-relaxed.text-gray-900")).getText();

            Review review = Review.builder()
                    .user(userList.get(new Random().nextInt(userList.size())))
                    .rating(rating)
                    .createdAt(createdAt)
                    .comment(comment)
                    .film(film)
                    .build();

            reviewList.add(review);
        }

        return reviewList;
    }

    public boolean isElementPresent(WebDriver driver, WebElement webElement, String cssSelector) {
        try {
            By locator = By.cssSelector(cssSelector);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(initWaitTime));
            wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(webElement, locator));
            return webElement.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }
}
