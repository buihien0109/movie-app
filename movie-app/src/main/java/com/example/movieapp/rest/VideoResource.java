package com.example.movieapp.rest;

import com.example.movieapp.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@RestController
@RequestMapping("api/videos")
@RequiredArgsConstructor
public class VideoResource {
    private final VideoService videoService;

    @GetMapping("/{fileName}")
    public ResponseEntity<ResourceRegion> getVideo(@PathVariable String fileName, HttpServletRequest request) {
        log.info("getVideo: {}", fileName);
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }

        try {
            ResourceRegion region = videoService.getResourceRegion(fileName, headers);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory.getMediaType(region.getResource()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(region);
        } catch (IOException e) {
            log.error("Error streaming video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(videoService.createVideo(file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Integer id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
