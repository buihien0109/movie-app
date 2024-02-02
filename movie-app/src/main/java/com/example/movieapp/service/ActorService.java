package com.example.movieapp.service;

import com.example.movieapp.entity.Actor;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.request.CreateActorRequest;
import com.example.movieapp.model.request.UpdateActorRequest;
import com.example.movieapp.repository.ActorRepository;
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
public class ActorService {
    private final FilmRepository filmRepository;
    private final ActorRepository actorRepository;
    private final FileService fileService;

    public List<Actor> getAllActors() {
        return actorRepository.findAll(Sort.by("createdAt").descending());
    }

    public Page<Actor> getAllActors(Integer page, Integer size) {
        return actorRepository.findAll(PageRequest.of(page, size));
    }

    public Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy diễn viên có id = " + id));
    }

    public Actor saveActor(CreateActorRequest request) {
        Actor actor = Actor.builder()
                .name(request.getName())
                .description(request.getDescription())
                .birthday(request.getBirthday())
                .avatar(StringUtils.generateLinkImage(request.getName()))
                .build();
        return actorRepository.save(actor);
    }

    public Actor updateActor(Integer id, UpdateActorRequest request) {
        Actor existingActor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy diễn viên có id = " + id));

        existingActor.setName(request.getName());
        existingActor.setDescription(request.getDescription());
        existingActor.setBirthday(request.getBirthday());
        return actorRepository.save(existingActor);
    }

    public void deleteActor(Integer id) {
        Actor existingActor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy diễn viên có id = " + id));

        // Kiểm tra xem diễn viên có đang áp dụng cho phim nào không. Nếu count > 0 thì không được xóa -> throw exception
        long count = filmRepository.countByActors_Id(id);
        if (count > 0) {
            throw new BadRequestException("Không thể xóa diễn viên này vì đang áp dụng cho phim");
        }

        // Kiểm tra xem diễn viên có avatar không. Nếu có thì xóa file avatar
        if (existingActor.getAvatar() != null) {
            FileUtils.deleteFile(existingActor.getAvatar());
        }

        // Xóa diễn viên
        actorRepository.deleteById(id);
    }

    public String updateAvatar(Integer actorId, MultipartFile file) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy diễn viên"));

        // Kiểm tra xem diễn viên có avatar không. Nếu có thì xóa file avatar sau đó lưu file mới
        if (actor.getAvatar() != null) {
            FileUtils.deleteFile(actor.getAvatar());
        }

        String filePath = fileService.saveFile(file);
        actor.setAvatar(filePath);
        actorRepository.save(actor);
        return actor.getAvatar();
    }
}
