package com.example.movieapp.utils.crawl;

import com.example.movieapp.entity.Blog;
import com.example.movieapp.entity.User;
import com.example.movieapp.model.enums.UserRole;
import com.example.movieapp.repository.BlogRepository;
import com.example.movieapp.repository.UserRepository;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogCrawler {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final Slugify slugify;

    public void crawlBlogPost(String url) {
        try {
            Random random = new Random();
            Document doc = Jsoup.connect(url).get();

            String title = doc.title();
            String slug = slugify.slugify(title);
            String description = doc.selectFirst("meta[name=description]").attr("content");
            Element contentElement = doc.selectFirst(".soju__prose.mx-auto.leading-normal").nextElementSibling();
            // cleanAttributes(contentElement);
            String content = contentElement.html();
            String thumbnail = doc.selectFirst("meta[property=og:image]").attr("content");

            // Random 1 user trong danh sách user ADMIN
            List<User> userList = userRepository.findByRole(UserRole.ADMIN);

            Blog blog = Blog.builder()
                    .title(title)
                    .slug(slug)
                    .description(description)
                    .content(content)
                    .thumbnail(thumbnail)
                    .user(userList.get(random.nextInt(userList.size())))
                    .status(true)
                    .build();

            log.info("Blog: {}", blog);
            blogRepository.save(blog);
        } catch (IOException e) {
            log.error("Error crawling blog post: {}", e.getMessage());
        }
    }

    private void cleanAttributes(Element element) {
        log.info("Containing HTML: {}", element.html());
        Elements allElements = element.getAllElements();

        for (Element el : allElements) {
            // el.clearAttributes(); // Remove all attributes
            el.removeAttr("class");
            el.removeAttr("id");
        }

        log.info("After cleaning attributes: {}", element);
    }

    public void crawlAllBlogPost() {
        List<String> urls = new ArrayList<>(List.of(
                "https://momo.vn/cinema/blog/huong-dan-xem-phim-marvel-danh-cho-nguoi-moi-bat-dau-758",
                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-4-1032",
                "https://momo.vn/cinema/blog/nhung-bo-phim-kinh-dien-khong-the-bo-qua-1030",
                "https://momo.vn/cinema/blog/top-anime-sieu-nhien-nhat-dinh-phai-xem-474",
                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-3-501",
                "https://momo.vn/cinema/blog/phim-han-quoc-2022-hap-dan-468",
                "https://momo.vn/cinema/blog/top-phim-xa-hoi-den-thai-lan-772",
                "https://momo.vn/cinema/blog/nhung-tac-pham-dien-anh-ve-la-ma-co-dai-khong-the-bo-lo-771",
                "https://momo.vn/cinema/blog/top-phim-ve-sat-nhan-hang-loat-khien-ban-rung-ron-497",
                "https://momo.vn/cinema/blog/top-phim-hay-nhat-cua-stephen-king-ban-nen-xem-ngay-496",
                "https://momo.vn/cinema/blog/top-phim-tvb-cung-dau-gay-can-tren-man-anh-485",
                "https://momo.vn/cinema/blog/nhung-bo-phim-hay-nhat-cua-chung-tu-don-482",
                "https://momo.vn/cinema/blog/nhung-bo-phim-ve-quy-satan-nhat-dinh-phai-xem-483",
                "https://momo.vn/cinema/blog/top-phim-trinh-tham-nhat-ban-loi-cuon-nhat-478",
                "https://momo.vn/cinema/blog/top-phim-trung-quoc-2022-dang-hua-hen-nhat-hien-nay-477",
                "https://momo.vn/cinema/blog/nhung-bo-phim-chu-de-tro-choi-dinh-cao-476",
                "https://momo.vn/cinema/blog/nhung-bo-phim-phap-hay-va-xuat-sac-nhat-473",
                "https://momo.vn/cinema/blog/top-phim-trinh-tham-tay-ban-nha-gay-can-tot-cung-471",
                "https://momo.vn/cinema/blog/top-phim-dien-anh-co-plottwist-khet-let-470",
                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-2-469",
                "https://momo.vn/cinema/blog/nhung-bo-phim-ve-ca-sau-dang-so-nhat-tren-man-anh-463",
                "https://momo.vn/cinema/blog/nhung-bo-phim-mao-danh-nhat-dinh-phai-xem-458",
                "https://momo.vn/cinema/blog/top-phim-anime-hay-nhat-moi-thoi-dai-457",
                "https://momo.vn/cinema/blog/top-phim-vuot-nguc-dinh-nhat-ma-ban-phai-xem-456",
                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-1-455",
                "https://momo.vn/cinema/blog/top-4-phim-dien-anh-lang-man-dang-xem-dip-giang-sinh-14",
                "https://momo.vn/cinema/blog/nhung-bo-phim-hay-ve-noi-co-don-khien-ban-bat-khoc-439",
                "https://momo.vn/cinema/blog/phim-hay-2022-cuc-dac-sac-moi-hang-loat-bom-tan-hanh-dong-199",
                "https://momo.vn/cinema/blog/nhung-bo-phim-hinh-su-tvb-hay-nhat-420",
                "https://momo.vn/cinema/blog/top-phim-co-doanh-thu-cao-nhat-moi-thoi-dai-415"
        ));

        for (String url : urls) {
            crawlBlogPost(url);
        }
    }
}
