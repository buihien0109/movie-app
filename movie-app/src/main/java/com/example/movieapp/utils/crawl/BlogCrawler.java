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
//                "https://momo.vn/cinema/blog/huong-dan-xem-phim-marvel-danh-cho-nguoi-moi-bat-dau-758",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-4-1032",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-kinh-dien-khong-the-bo-qua-1030",
//                "https://momo.vn/cinema/blog/top-anime-sieu-nhien-nhat-dinh-phai-xem-474",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-3-501",
//                "https://momo.vn/cinema/blog/phim-han-quoc-2022-hap-dan-468",
//                "https://momo.vn/cinema/blog/top-phim-xa-hoi-den-thai-lan-772",
//                "https://momo.vn/cinema/blog/nhung-tac-pham-dien-anh-ve-la-ma-co-dai-khong-the-bo-lo-771",
//                "https://momo.vn/cinema/blog/top-phim-ve-sat-nhan-hang-loat-khien-ban-rung-ron-497",
//                "https://momo.vn/cinema/blog/top-phim-hay-nhat-cua-stephen-king-ban-nen-xem-ngay-496",
//                "https://momo.vn/cinema/blog/top-phim-tvb-cung-dau-gay-can-tren-man-anh-485",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-hay-nhat-cua-chung-tu-don-482",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-ve-quy-satan-nhat-dinh-phai-xem-483",
//                "https://momo.vn/cinema/blog/top-phim-trinh-tham-nhat-ban-loi-cuon-nhat-478",
//                "https://momo.vn/cinema/blog/top-phim-trung-quoc-2022-dang-hua-hen-nhat-hien-nay-477",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-chu-de-tro-choi-dinh-cao-476",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-phap-hay-va-xuat-sac-nhat-473",
//                "https://momo.vn/cinema/blog/top-phim-trinh-tham-tay-ban-nha-gay-can-tot-cung-471",
//                "https://momo.vn/cinema/blog/top-phim-dien-anh-co-plottwist-khet-let-470",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-2-469",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-ve-ca-sau-dang-so-nhat-tren-man-anh-463",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-mao-danh-nhat-dinh-phai-xem-458",
//                "https://momo.vn/cinema/blog/top-phim-anime-hay-nhat-moi-thoi-dai-457",
//                "https://momo.vn/cinema/blog/top-phim-vuot-nguc-dinh-nhat-ma-ban-phai-xem-456",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-1-455",
//                "https://momo.vn/cinema/blog/top-4-phim-dien-anh-lang-man-dang-xem-dip-giang-sinh-14",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-hay-ve-noi-co-don-khien-ban-bat-khoc-439",
//                "https://momo.vn/cinema/blog/phim-hay-2022-cuc-dac-sac-moi-hang-loat-bom-tan-hanh-dong-199",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-hinh-su-tvb-hay-nhat-420",
//                "https://momo.vn/cinema/blog/top-phim-co-doanh-thu-cao-nhat-moi-thoi-dai-415"
//                "https://momo.vn/cinema/blog/phim-tam-ly-han-quoc-nghet-tho-355",
//                "https://momo.vn/cinema/blog/phim-hinh-su-toi-pham-han-quoc-nhat-dinh-phai-xem-346",
//                "https://momo.vn/cinema/blog/phim-ma-ca-rong-hay-nhat-cua-dien-anh-the-gioi-345",
//                "https://momo.vn/cinema/blog/top-phim-ma-han-quoc-khong-nen-xem-mot-minh-343",
//                "https://momo.vn/cinema/blog/phim-vo-thuat-dinh-cao-chac-chan-lam-ban-say-me-339",
//                "https://momo.vn/cinema/blog/phim-khoa-hoc-vien-tuong-dang-xem-338",
//                "https://momo.vn/cinema/blog/top-phim-tham-hoa-thien-nhien-326",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-9-357",
//                "https://momo.vn/cinema/blog/top-phim-zombie-hay-nhat-tren-man-anh-325",
//                "https://momo.vn/cinema/blog/nhung-sieu-trom-tren-man-anh-khien-ban-di-tu-bat-ngo-nay-den-kinh-ngac-khac-159",
//                "https://momo.vn/cinema/blog/top-phim-kinh-di-kich-tinh-hay-323",
//                "https://momo.vn/cinema/blog/top-phim-tinh-cam-han-quoc-ngot-ngao-nhat-tren-man-anh-320",
//                "https://momo.vn/cinema/blog/top-phim-dua-xe-hay-va-gay-can-319",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hoat-hinh-hay-nhat-the-gioi-318",
//                "https://momo.vn/cinema/blog/top-phim-nguoi-ngoai-hanh-tinh-dang-xem-nhat-hien-nay-316",
//                "https://momo.vn/cinema/blog/top-phim-trinh-tham-hay-nhat-moi-thoi-dai-312",
//                "https://momo.vn/cinema/blog/top-phim-thai-lan-duoc-mong-cho-nam-2021-292",
//                "https://momo.vn/cinema/blog/top-phim-ngon-tinh-lang-man-hay-khong-nen-bo-qua-286",
//                "https://momo.vn/cinema/blog/top-phim-hong-kong-hay-nhat-moi-thoi-dai-306",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-8-313",
//                "https://momo.vn/cinema/blog/danh-sach-phim-tinh-cam-my-khien-ban-khoc-uot-goi-254",
//                "https://momo.vn/cinema/blog/top-phim-kiem-hiep-xem-nhieu-nhat-233",
//                "https://momo.vn/cinema/blog/loat-phim-toi-pham-hay-nhat-tren-netflix-hien-nay-219",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-7-255",
//                "https://momo.vn/cinema/blog/loat-phim-han-se-gay-chan-dong-trong-nam-2021-200",
//                "https://momo.vn/cinema/blog/phim-bom-tan-2020-top-15-phim-dien-anh-dang-mong-cho-nhat-nam-84",
//                "https://momo.vn/cinema/blog/nhung-phim-hay-nhat-cua-ghibli-dang-trinh-chieu-tren-netflix-131",
//                "https://momo.vn/cinema/blog/top-nhung-bo-phim-tvb-dang-xem-nhat-da-tro-thanh-huyen-thoai-139",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hai-nao-hap-dan-va-gay-can-den-tung-giay-phan-1-147",
//                "https://momo.vn/cinema/blog/top-10-phim-truyen-hinh-han-quoc-duoc-yeu-thich-nhat-tai-viet-nam-91",
//                "https://momo.vn/cinema/blog/nhung-bo-phim-hai-hanh-dong-man-nhan-nguoi-xem-trong-thang-2-2020-90",
//                "https://momo.vn/cinema/blog/danh-sach-phim-hay-netflix-thang-6-224",
//                "https://momo.vn/cinema/blog/loat-phim-tay-ban-nha-xung-dang-quoc-bao-rating-khien-nuoc-nha-phai-tu-hao-126",
//                "https://momo.vn/cinema/blog/danh-sach-phim-netflix-thang-5-201",
                "https://momo.vn/cinema/blog/6-bo-phim-huyen-thoai-cua-dao-dien-xuat-sac-vuong-gia-ve-164",
                "https://momo.vn/cinema/blog/top-nhung-bo-phim-khoa-hoc-vien-tuong-dang-xem-nhat-the-ky-21-158",
                "https://momo.vn/cinema/blog/danh-sach-phim-gangster-kinh-dien-ma-ban-khong-the-bo-qua-157",
                "https://momo.vn/cinema/blog/nhung-vai-dien-xuat-than-cua-leonardo-dicaprio-khien-ban-xem-toi-dau-me-den-do-143",
                "https://momo.vn/cinema/blog/hay-nhin-lai-nhung-vai-dien-de-doi-trong-su-nghiep-cua-tai-tu-hollywood-brad-pitt-142",
                "https://momo.vn/cinema/blog/top-sieu-pham-kinh-di-dang-mong-cho-trong-mua-he-2020-132",
                "https://momo.vn/cinema/blog/top-phim-lung-lay-an-tuong-nhat-cua-hang-phim-oscar-a24-128",
                "https://momo.vn/cinema/blog/diem-danh-phim-truyen-hinh-hoa-ngu-duoc-mong-cho-khoi-chieu-vao-cuoi-nam-2020-127",
                "https://momo.vn/cinema/blog/top-phim-kinh-di-dang-mong-doi-nhat-2020-124",
                "https://momo.vn/cinema/blog/top-phim-me-chong-nang-dau-tao-nghiep-nhat-man-anh-viet-119",
                "https://momo.vn/cinema/blog/top-phim-bom-tan-hay-nhat-cua-vu-tru-dien-anh-dc-117",
                "https://momo.vn/cinema/blog/danh-sach-phim-bom-tan-2020-bi-hoan-chieu-vo-thoi-han-114"
        ));

        for (String url : urls) {
            crawlBlogPost(url);
        }
    }
}
