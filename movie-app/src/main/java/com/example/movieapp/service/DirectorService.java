package com.example.movieapp.service;

import com.example.movieapp.entity.Director;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.request.CreateDirectorRequest;
import com.example.movieapp.model.request.UpdateDirectorRequest;
import com.example.movieapp.repository.DirectorRepository;
import com.example.movieapp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;
    private final FileService fileService;

    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    public Page<Director> getAllDirectors(Integer page, Integer size) {
        return directorRepository.findAll(PageRequest.of(page, size));
    }

    public Director getDirectorById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy đạo diễn có id = " + id));
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
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy đạo diễn có id = " + id));

        existingDirector.setName(request.getName());
        existingDirector.setDescription(request.getDescription());
        existingDirector.setBirthday(request.getBirthday());
        return directorRepository.save(existingDirector);
    }

    public void deleteDirector(Integer id) {
        Director existingDirector = directorRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy đạo diễn có id = " + id));
        directorRepository.deleteById(id);
    }

    public String updateAvatar(Integer directorId, MultipartFile file) {
        Director director = directorRepository.findById(directorId)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy đạo diễn"));

        String filePath = fileService.saveFile(file);
        director.setAvatar(filePath);
        directorRepository.save(director);
        return director.getAvatar();
    }
}
