package com.example.movieapp.service;

import com.example.movieapp.entity.Video;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.helper.VideoInfo;
import com.example.movieapp.repository.VideoRepository;
import com.example.movieapp.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private static final long CHUNK_SIZE = 1000000L;
    public static final String uploadDir = "video_uploads";

    public VideoService(VideoRepository videoRepository) {
        FileUtils.createDirectory(uploadDir);
        this.videoRepository = videoRepository;
    }

    public Video createVideo(MultipartFile file) {
        CompletableFuture<Video> videoFuture = processVideo(file);
        Video video = videoFuture.join();
        return videoRepository.save(video);
    }

    public Video saveVideo(Video video) {
        return videoRepository.save(video);
    }

    @Async
    public CompletableFuture<Video> processVideo(MultipartFile file) {
        try {
            // Kiểm tra file có phải là video hay không
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            if (!mimeType.startsWith("video")) {
                throw new BadRequestException("File không phải là video");
            }

            // Xử lý lưu trữ video
            String fileName = UUID.randomUUID().toString();
            Path path = Paths.get(uploadDir + "/" + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Nén video
            // compressVideo(path);

            // Lấy thông tin video
            VideoInfo videoInfo = getVideoInfo(path.toString());

            // Lưu video vào database
            Video video = Video.builder()
                    .name(file.getOriginalFilename())
                    .url("/api/videos/" + fileName)
                    .duration(videoInfo.getDuration())
                    .size(extractSize(file))
                    .build();

            // Lấy thông tin video
            return CompletableFuture.completedFuture(video);
        } catch (Exception e) {
            log.error("Không thể xử lý video");
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private void compressVideo(Path videoPath) {
        log.info("Đang nén video");
        try {
            String outputFilePath = videoPath.toString().replace(".mp4", "_compressed.mp4");

            // Lệnh FFmpeg để nén video
            String ffmpegCmd = "ffmpeg -i " + videoPath.toString() + " -vcodec h264 -acodec aac " + outputFilePath;

            // Thực thi lệnh FFmpeg
            Process process = Runtime.getRuntime().exec(ffmpegCmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Xuất log cho mục đích debug
            }

            process.waitFor();
        } catch (Exception e) {
            log.error("Không thể nén video");
            log.error(e.getMessage());
        }
    }

    public ResourceRegion getResourceRegion(String fileName, HttpHeaders headers) throws IOException {
        UrlResource video = new UrlResource("file:" + uploadDir + "/" + fileName);
        if (!video.exists() || !video.isReadable()) {
            throw new FileNotFoundException("Could not read file: " + fileName);
        }

        return createResourceRegion(video, headers);
    }

    private ResourceRegion createResourceRegion(UrlResource video, HttpHeaders headers) throws IOException {
        long contentLength = video.contentLength();
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(CHUNK_SIZE, end - start + 1);
            return new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Math.min(CHUNK_SIZE, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }

    // Lấy thông tin video
    public VideoInfo getVideoInfo(String videoPath) throws Exception {
        log.info("Đang lấy thông tin video");

        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", videoPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        VideoInfo videoInfo = new VideoInfo();

        while ((line = reader.readLine()) != null) {
            if (line.contains("Duration")) {
                videoInfo.setDuration(extractDuration(line));
            }
            if (line.contains("Video")) {
                videoInfo.setFormat(extractFormat(line));
                videoInfo.setResolution(extractResolution(line));
            }
        }

        process.waitFor();
        return videoInfo;
    }

    // Phương thức này sẽ trích xuất và trả về thời lượng của video dưới dạng số giây.
    private Integer extractDuration(String line) {
        log.info("Đang trích xuất thời lượng video");

        Pattern pattern = Pattern.compile("Duration: (\\d+):(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            int hours = Integer.parseInt(matcher.group(1));
            int minutes = Integer.parseInt(matcher.group(2));
            int seconds = Integer.parseInt(matcher.group(3));
            return hours * 3600 + minutes * 60 + seconds;
        }
        return null;
    }

    // Phương thức này sẽ trích xuất và trả về định dạng video.
    private String extractFormat(String line) {
        log.info("Đang trích xuất định dạng video");

        Pattern pattern = Pattern.compile("Video: (\\w+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // Phương thức này sẽ trích xuất và trả về độ phân giải của video.
    private String extractResolution(String line) {
        log.info("Đang trích xuất độ phân giải video");

        Pattern pattern = Pattern.compile(", (\\d+x\\d+)[, ]");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    // Tính toán kích thước của file
    public double extractSize(MultipartFile file) {
        log.info("Đang tính toán kích thước video");

        long sizeInBytes = file.getSize();

        // làm tròn 2 dấu phay động
        return Math.round((double) sizeInBytes / (1024 * 1024) * 100) / 100.0;
    }

    public void deleteVideo(Integer id) {
        // Xóa video trong database
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video không tồn tại"));
        videoRepository.deleteById(id);

        // Xóa video trong thư mục lưu trữ
        String videoPath = uploadDir + "/" + video.getUrl().substring(video.getUrl().lastIndexOf("/") + 1);
        FileUtils.deleteFile(videoPath);
    }
}
