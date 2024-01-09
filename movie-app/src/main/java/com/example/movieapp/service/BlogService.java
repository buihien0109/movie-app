package com.example.movieapp.service;

import com.example.movieapp.entity.Blog;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.request.UpsertBlogRequest;
import com.example.movieapp.repository.BlogRepository;
import com.example.movieapp.repository.UserRepository;
import com.example.movieapp.utils.StringUtils;
import com.github.slugify.Slugify;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final Slugify slugify;
    private final HttpSession session;

    // admin get all blogs
    @Transactional
    public List<Blog> getAllBlogsAdmin() {
        return blogRepository.findAll(Sort.by("createdAt").descending());
    }

    // admin get all blog of logged user
    public List<Blog> getAllOwnBlogAdmin() {
        User user = (User) session.getAttribute("currentUser");
        return blogRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    public Blog getBlogById(Integer id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy bài viết với id = " + id));
    }

    public Blog saveBlog(UpsertBlogRequest request) {
        User user = (User) session.getAttribute("currentUser");

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
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy bài viết với id = " + id));

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
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy bài viết với id = " + id));

        blogRepository.delete(blog);
    }

    public String updateThumbnail(Integer id, MultipartFile file) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy bài viết với id = " + id));

        String filePath = fileService.saveFile(file);
        blog.setThumbnail(filePath);
        blogRepository.save(blog);
        return blog.getThumbnail();
    }
}
