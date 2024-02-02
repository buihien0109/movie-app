package com.example.movieapp.service;

import com.example.movieapp.entity.Director;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.request.CreateDirectorRequest;
import com.example.movieapp.model.request.UpdateDirectorRequest;
import com.example.movieapp.repository.DirectorRepository;
import com.example.movieapp.repository.FilmRepository;
import com.example.movieapp.utils.FileUtils;
import com.example.movieapp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;
    private final FileService fileService;

    public List<Director> getAllDirectors() {
        return directorRepository.findAll(Sort.by("createdAt").descending());
    }

    public Page<Director> getAllDirectors(Integer page, Integer size) {
        return directorRepository.findAll(PageRequest.of(page, size));
    }

    public Director getDirectorById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đạo diễn có id = " + id));
    }

    public Director saveDirector(CreateDirectorRequest request) {
        Director director = Director.builder()
                .name(request.getName())
                .description(request.getDescription())
                .birthday(request.getBirthday())
                .avatar(StringUtils.generateLinkImage(request.getName()))
                .build();
        return directorRepository.save(director);
    }

    public Director updateDirector(Integer id, UpdateDirectorRequest request) {
        Director existingDirector = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đạo diễn có id = " + id));

        existingDirector.setName(request.getName());
        existingDirector.setDescription(request.getDescription());
        existingDirector.setBirthday(request.getBirthday());
        return directorRepository.save(existingDirector);
    }

    public void deleteDirector(Integer id) {
        Director existingDirector = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đạo diễn có id = " + id));

        // Kiểm tra xem đạo diễn có đang áp dụng cho phim nào không. Nếu count > 0 thì không được xóa -> throw exception
        long count = filmRepository.countByDirectors_Id(id);
        if (count > 0) {
            throw new BadRequestException("Không thể xóa đạo diễn này vì đang áp dụng cho phim");
        }

        // Kiểm tra xem đạo diễn có avatar không. Nếu có thì xóa file avatar
        if (existingDirector.getAvatar() != null) {
            FileUtils.deleteFile(existingDirector.getAvatar());
        }

        // Xóa đạo diễn
        directorRepository.deleteById(id);
    }

    public String updateAvatar(Integer directorId, MultipartFile file) {
        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đạo diễn"));

        // Kiểm tra xem đạo diễn có avatar không. Nếu có thì xóa file avatar sau đó lưu file mới
        if (director.getAvatar() != null) {
            FileUtils.deleteFile(director.getAvatar());
        }

        String filePath = fileService.saveFile(file);
        director.setAvatar(filePath);
        directorRepository.save(director);
        return director.getAvatar();
    }
}
