package com.example.movieapp.rest;

import com.example.movieapp.model.request.UpsertBlogRequest;
import com.example.movieapp.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/admin/blogs")
@RequiredArgsConstructor
public class BlogResource {
    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<?> getAllBlogs(@RequestParam("start") int start,
                                         @RequestParam("length") int length,
                                         @RequestParam("draw") int draw,
                                         @RequestParam("order[0][column]") int sortColumn,
                                         @RequestParam("order[0][dir]") String sortOrder) {
        return ResponseEntity.ok(blogService.getAllBlogsAdmin(start, length, draw, sortColumn, sortOrder));
    }

    @GetMapping("/own-blogs")
    public ResponseEntity<?> getAllOwnBlogs(@RequestParam("start") int start,
                                            @RequestParam("length") int length,
                                            @RequestParam("draw") int draw,
                                            @RequestParam("order[0][column]") int sortColumn,
                                            @RequestParam("order[0][dir]") String sortOrder) {
        return ResponseEntity.ok(blogService.getAllOwnBlogsAdmin(start, length, draw, sortColumn, sortOrder));
    }

    @PostMapping
    public ResponseEntity<?> createBlog(@Valid @RequestBody UpsertBlogRequest request) {
        return new ResponseEntity<>(blogService.saveBlog(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable Integer id, @Valid @RequestBody UpsertBlogRequest request) {
        return ResponseEntity.ok(blogService.updateBlog(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Integer id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/update-thumbnail")
    public ResponseEntity<?> updateThumbnail(@RequestParam("file") MultipartFile file, @PathVariable Integer id) {
        return ResponseEntity.ok(blogService.updateThumbnail(id, file));
    }
}
