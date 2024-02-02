package com.example.movieapp.service;

import com.example.movieapp.constant.Constant;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResourceNotFoundException;
import com.example.movieapp.model.request.CreateUserRequest;
import com.example.movieapp.model.request.UpdatePasswordRequest;
import com.example.movieapp.model.request.UpdateProfileUserRequest;
import com.example.movieapp.model.request.UpdateUserRequest;
import com.example.movieapp.repository.UserRepository;
import com.example.movieapp.security.SecurityUtils;
import com.example.movieapp.utils.FileUtils;
import com.example.movieapp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("createdAt").descending());
    }

    public Page<User> getAllUsers(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user có id = " + id));
    }

    public User saveUser(CreateUserRequest request) {
        // find user by email -> throw exception if exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email đã tồn tại");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(Constant.DEFAULT_PASSWORD))
                .avatar(StringUtils.generateLinkImage(request.getName()))
                .role(request.getRole())
                .build();
        return userRepository.save(user);
    }

    public User updateUser(Integer id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user có id = " + id));

        existingUser.setName(request.getName());
        existingUser.setPhone(request.getPhone());
        existingUser.setRole(request.getRole());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user có id = " + id));

        // Kiểm tra xem user có avatar không. Nếu có thì xóa file avatar
        if (existingUser.getAvatar() != null) {
            FileUtils.deleteFile(existingUser.getAvatar());
        }
        userRepository.deleteById(id);
    }

    public String updateAvatar(Integer userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        // Kiểm tra xem user có avatar không. Nếu có thì xóa file avatar sau đó lưu file mới
        if (user.getAvatar() != null) {
            FileUtils.deleteFile(user.getAvatar());
        }

        String filePath = fileService.saveFile(file);
        user.setAvatar(filePath);
        userRepository.save(user);
        return user.getAvatar();
    }

    public String updateAvatar(MultipartFile file) {
        User user = SecurityUtils.getCurrentUserLogin();

        // Kiểm tra xem user có avatar không. Nếu có thì xóa file avatar sau đó lưu file mới
        if (user.getAvatar() != null) {
            FileUtils.deleteFile(user.getAvatar());
        }
        String filePath = fileService.saveFile(file);
        user.setAvatar(filePath);
        userRepository.save(user);
        return user.getAvatar();
    }

    public String resetPassword(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        user.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userRepository.save(user);
        return Constant.DEFAULT_PASSWORD;
    }

    public void updatePassword(UpdatePasswordRequest request) {
        User user = SecurityUtils.getCurrentUserLogin();

        // check old password correct
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không đúng");
        }

        // check new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // check new password and old password match
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void updateProfile(UpdateProfileUserRequest request) {
        User user = SecurityUtils.getCurrentUserLogin();
        user.setName(request.getName());
        user.setPhone(request.getPhone());

        userRepository.save(user);
    }
}
