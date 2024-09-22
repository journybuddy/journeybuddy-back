package journeybuddy.spring.service.user;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.converter.user.UserUpdateConverter;
import journeybuddy.spring.domain.user.Role;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.user.RoleRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.service.community.post.S3ImageService;
import journeybuddy.spring.web.dto.user.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static journeybuddy.spring.apiPayload.code.status.ErrorStatus.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private static final Logger log = LoggerFactory.getLogger(UserCommandServiceImpl.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final S3ImageService s3ImageService;

    @Override
    public User addUser(UserRequestDTO.RegisterDTO request) {

        User addUser = UserUpdateConverter.toUser(request, bCryptPasswordEncoder);


        Role defaultRole = roleRepository.findByName("USER")
                .orElse(null);


        if (defaultRole != null) {
            addUser.setRoles(Collections.singletonList(defaultRole));
        } else {
            addUser.setRoles(Collections.emptyList());
        }


        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }


        return userRepository.save(addUser);
    }


    @Override
    public User updateUser(UserRequestDTO.UpdateDTO request, MultipartFile profileImage, String userEmail) {
        User existingUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));


        if (profileImage != null && !profileImage.isEmpty()) {
            try {

                String imageUrl = uploadImage(profileImage);
                existingUser.setProfile_image(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Profile image upload failed", e);
            }
        } else if (request.getProfile_image() != null && !request.getProfile_image().isEmpty()) {

            existingUser.setProfile_image(request.getProfile_image());
        }


        existingUser.setBio(request.getBio());
        return userRepository.save(existingUser);
    }
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    public User deletedById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new TempHandler(MEMBER_NOT_FOUND);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id: " + id);
        return null;
    }

    @Override
    public Long loginCheck(UserRequestDTO.RegisterDTO request) {
        User loginUser = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (loginUser != null &&  bCryptPasswordEncoder.matches(request.getPassword(), loginUser.getPassword())) {
            log.info("로그인한 유저:" + loginUser.getId());
            return loginUser.getId();
        }
        return null;
    }

    @Override
    public boolean EmailDuplicationCheck(UserRequestDTO.RegisterDTO request) {
        return userRepository.findByEmail(request.getEmail()).isPresent();
    }

    @Override
    public String getUserEmailById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return user.getEmail();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new TempHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }


    public String uploadImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be null or empty");
        }
        try {
            return s3ImageService.upload(image, "profile-images");
        } catch (IOException e) {
            throw new IOException("Failed to upload image: " + image.getOriginalFilename(), e);
        }
    }



}
