package com.example.movieapp.service;

import com.example.movieapp.entity.Actor;
import com.example.movieapp.entity.Genre;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.request.CreateActorRequest;
import com.example.movieapp.model.request.UpdateActorRequest;
import com.example.movieapp.repository.ActorRepository;
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
public class ActorService {
    private final ActorRepository actorRepository;
    private final FileService fileService;

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Page<Actor> getAllActors(Integer page, Integer size) {
        return actorRepository.findAll(PageRequest.of(page, size));
    }

    public Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy diễn viên có id = " + id));
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
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy diễn viên có id = " + id));

        existingActor.setName(request.getName());
        existingActor.setDescription(request.getDescription());
        existingActor.setBirthday(request.getBirthday());
        return actorRepository.save(existingActor);
    }

    public void deleteActor(Integer id) {
        Actor existingActor = actorRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy diễn viên có id = " + id));
        actorRepository.deleteById(id);
    }

    public String updateAvatar(Integer actorId, MultipartFile file) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy diễn viên"));

        String filePath = fileService.saveFile(file);
        actor.setAvatar(filePath);
        actorRepository.save(actor);
        return actor.getAvatar();
    }
}
