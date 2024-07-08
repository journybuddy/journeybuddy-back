package journeybuddy.spring.web.controller;

import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.UserService.UserCommandService;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user")
public class UserRestController {

    private final UserCommandService userCommandService;

    @GetMapping("/register")
    public ResponseEntity<UserRequestDTO.UpdateDTO> getRegistrationForm() {
        return ResponseEntity.ok(new UserRequestDTO.UpdateDTO());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrationForm(@RequestBody @Valid UserRequestDTO.UpdateDTO request,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            User newUser = userCommandService.addUser(request);
            Map<String, Long> response = new HashMap<>();
            response.put("userId", newUser.getId());
            log.info("회원가입 완료");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원가입 중 문제 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 문제가 발생했습니다.");
        }
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<?> updateForm(@PathVariable("userId") Long userId,
                                        @RequestBody @Valid UserRequestDTO.UpdateDTO request,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            request.setId(userId);
            userCommandService.updateUser(userDetails, request);
            log.info("사용자 업데이트 완료");
            return ResponseEntity.ok().build();
        } catch (TempHandler e) {
            log.error("사용자 업데이트 실패: {}", e.getErrorReasonHttpStatus());
            return ResponseEntity.status(Integer.parseInt(e.getMessage())).body("사용자 업데이트 중 문제가 발생했습니다.");
        }
    }

    @GetMapping("/update/{userId}")
    public ResponseEntity<?> showUpdateForm(@PathVariable("userId") Long userId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Long id = Long.valueOf(userDetails.getUsername());
        if (!id.equals(userId)) {
            log.error("접근 권한 없는 사용자");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한 없음");
        }
        try {
            UserResponseDTO.UpdateResultDTO userDTO = UserUpdateConverter.toUpdateResultDTO(userCommandService.getUserById(userId));
            log.info("접근 권한 있는 사용자");
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            log.error("접근 권한 확인 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> getLoginForm() {
        return ResponseEntity.ok("Login page");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginForm(@RequestBody @Valid UserRequestDTO.UpdateDTO request, HttpSession httpSession) {
        Long id = userCommandService.loginCheck(request, httpSession);
        if (id != null) {
            log.info("로그인한 사용자: {}", request.getEmail());
            Map<String, Long> response = new HashMap<>();
            response.put("userId", id);
            return ResponseEntity.ok(response);
        } else {
            log.error("존재하지 않는 사용자: {}", request.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 사용자");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession httpSession) {
        httpSession.invalidate();
        log.info("로그아웃 됨");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId, HttpSession httpSession) {
        userCommandService.deletedById(userId);
        httpSession.invalidate();
        log.info("탈퇴 처리 완료");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/home")
    public ResponseEntity<String> showHome() {
        return ResponseEntity.ok("Home page");
    }
}
/*

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
public class UserRestController {

    private final UserCommandService userCommandService;

    private final UserRepository userRepository;

    @PostMapping("/add")
    @ApiOperation(value = "Add a new user")
    public ApiResponse<UserResponseDTO.UpdateResultDTO> addUser(@RequestBody @Valid UserRequestDTO.UpdateDTO request) {

        if(userRepository.existsByEmail(request.getEmail())){
            return ApiResponse.onFailure("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.", null);
        }else{
            User addUser = userCommandService.addUser(request);
        return ApiResponse.onSuccess(UserUpdateConverter.toUpdateResultDTO(addUser));
        }
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponseDTO.UpdateResultDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO.UpdateResultDTO> result = users.stream()
                .map(UserUpdateConverter::toUpdateResultDTO)
                .collect(Collectors.toList());
        return ApiResponse.onSuccess(result);
    }

    @GetMapping
    public ApiResponse<UserResponseDTO.UpdateResultDTO> getUserById(@RequestParam("id") Long userId) {
        User user = userCommandService.getUserById(userId);
        if (user == null) {
            throw new TempHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        return ApiResponse.onSuccess(UserUpdateConverter.toUpdateResultDTO(user));
    }

    @PutMapping("/update/{userId}")
    @ApiOperation(value = "update a new user")
    public ApiResponse<UserResponseDTO.UpdateResultDTO> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid UserRequestDTO.UpdateDTO request) {
        request.setId(userId);

        if(userRepository.existsByEmail(request.getEmail())){
            return ApiResponse.onFailure("DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.", null);
        }
        else{
            User updatedUser = userCommandService.updateUser(request);
        return ApiResponse.onSuccess(UserUpdateConverter.toUpdateResultDTO(updatedUser));
        }
    }

    @DeleteMapping
    @ApiOperation(value = "회원탈퇴기능")
    public ApiResponse<Object> deleteUser(@RequestParam("id") Long userId) {
        User deletedUser = userCommandService.deletedById(userId);
        return ApiResponse.deleteSuccess();
    }

 */