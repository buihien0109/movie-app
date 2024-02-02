package com.example.movieapp.service;

import com.example.movieapp.entity.Blog;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.dto.BlogDto;
import com.example.movieapp.model.request.UpsertBlogRequest;
import com.example.movieapp.repository.BlogRepository;
import com.example.movieapp.security.SecurityUtils;
import com.example.movieapp.utils.FileUtils;
import com.example.movieapp.utils.StringUtils;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final FileService fileService;
    private final Slugify slugify;

    // Lấy danh sách bài viết mới nhất theo số lượng và ngày publish nhưng không có chứa bài viết đang xem
    public Page<BlogDto> getNewestBlogs(int page, int size, Integer blogId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return blogRepository.findByIdNotAndStatus(blogId, true, pageable);
    }

    // admin get all blogs
    @Transactional
    public Map<String, Object> getAllBlogsAdmin(int start, int length, int draw, int sortColumn, String sortOrder) {
        log.info("start: {}, length: {}, draw: {}, sortColumn: {}, sortOrder: {}", start, length, draw, sortColumn, sortOrder);

        Sort sort = getSort(sortColumn, sortOrder);
        Pageable pageable = PageRequest.of(start / length, length, sort);
        Page<Blog> pageData = blogRepository.findAll(pageable);

        return getData(draw, pageData);
    }

    // admin get all blog of logged user
    public Map<String, Object> getAllOwnBlogsAdmin(int start, int length, int draw, int sortColumn, String sortOrder) {
        log.info("start: {}, length: {}, draw: {}, sortColumn: {}, sortOrder: {}", start, length, draw, sortColumn, sortOrder);
        User user = SecurityUtils.getCurrentUserLogin();

        Sort sort = getSortOwnBlog(sortColumn, sortOrder);
        Pageable pageable = PageRequest.of(start / length, length, sort);
        Page<Blog> pageData = blogRepository.findByUser_Id(user.getId(), pageable);

        return getData(draw, pageData);
    }

    private Map<String, Object> getData(int draw, Page<Blog> pageData) {
        Map<String, Object> data = new HashMap<>();
        data.put("draw", draw);
        data.put("recordsTotal", pageData.getTotalElements());
        data.put("recordsFiltered", pageData.getTotalElements());
        data.put("data", pageData.getContent());

        return data;
    }

    private Sort getSort(int column, String dir) {
        String sortField = switch (column) {
            case 0 -> "title";
            case 1 -> "user.name";
            case 2 -> "status";
            case 3 -> "createdAt";
            default -> "createdAt"; // Mặc định sắp xếp theo title
            // Các trường hợp khác tùy theo cấu trúc của bảng
        };
        return Sort.by(Sort.Direction.fromString(dir), sortField);
    }

    private Sort getSortOwnBlog(int column, String dir) {
        String sortField = switch (column) {
            case 0 -> "title";
            case 1 -> "status";
            case 2 -> "createdAt";
            default -> "createdAt"; // Mặc định sắp xếp theo title
            // Các trường hợp khác tùy theo cấu trúc của bảng
        };
        return Sort.by(Sort.Direction.fromString(dir), sortField);
    }

    public Blog getBlogById(Integer id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với id = " + id));
    }

    public Blog saveBlog(UpsertBlogRequest request) {
        User user = SecurityUtils.getCurrentUserLogin();

        // Create blog
        Blog blog = Blog.builder()
                .title(request.getTitle())
                .slug(slugify.slugify(request.getTitle()))
                .content(request.getContent())
                .description(request.getDescription())
                .status(request.getStatus())
                .user(user)
                .thumbnail(StringUtils.generateLinkImage(request.getTitle()))
                .build();

        return blogRepository.save(blog);
    }

    public Blog updateBlog(Integer id, UpsertBlogRequest request) {
        // find blog by id
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với id = " + id));

        // update blog
        blog.setTitle(request.getTitle());
        blog.setSlug(slugify.slugify(request.getTitle()));
        blog.setDescription(request.getDescription());
        blog.setContent(request.getContent());
        blog.setStatus(request.getStatus());

        return blogRepository.save(blog);
    }

    public void deleteBlog(Integer id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với id = " + id));

        // Kiểm tra xem blog có thumbnail không. Nếu có thì xóa file thumbnail
        if (blog.getThumbnail() != null) {
            FileUtils.deleteFile(blog.getThumbnail());
        }

        blogRepository.delete(blog);
    }

    public String updateThumbnail(Integer id, MultipartFile file) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết với id = " + id));

        // Kiểm tra xem blog có thumbnail không. Nếu có thì xóa file thumbnail sau đó lưu file mới
        if (blog.getThumbnail() != null) {
            FileUtils.deleteFile(blog.getThumbnail());
        }

        String filePath = fileService.saveFile(file);
        blog.setThumbnail(filePath);
        blogRepository.save(blog);
        return blog.getThumbnail();
    }
}
