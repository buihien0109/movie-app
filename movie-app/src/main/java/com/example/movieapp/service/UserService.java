package com.example.movieapp.service;

import com.example.movieapp.constant.Constant;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.request.CreateUserRequest;
import com.example.movieapp.model.request.UpdateUserRequest;
import com.example.movieapp.repository.UserRepository;
import com.example.movieapp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsers(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy user có id = " + id));
    }

    public User saveUser(CreateUserRequest request) {
        // find user by email -> throw exception if exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email đã tồn tại");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(Constant.DEFAULT_PASSWORD))
                .avatar(StringUtils.generateLinkImage(request.getName()))
                .role(request.getRole())
                .build();
        return userRepository.save(user);
    }

    public User updateUser(Integer id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy user có id = " + id));

        existingUser.setName(request.getName());
        existingUser.setRole(request.getRole());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy user có id = " + id));
        userRepository.deleteById(id);
    }

    public String updateAvatar(Integer userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy user"));

        String filePath = fileService.saveFile(file);
        user.setAvatar(filePath);
        userRepository.save(user);
        return user.getAvatar();
    }

    public String resetPassword(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy user"));

        user.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userRepository.save(user);
        return Constant.DEFAULT_PASSWORD;
    }
}
