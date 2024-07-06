package journeybuddy.spring.web.controller;
/*
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import journeybuddy.spring.apiPayload.code.BaseErrorCode;
import journeybuddy.spring.apiPayload.code.status.ErrorStatus;
import journeybuddy.spring.apiPayload.exception.handler.TempHandler;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.service.UserService.UserCommandService;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import journeybuddy.spring.web.dto.UserDTO.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final SecurityContextRepository securityContextRepository;
    private final UserCommandService userCommandService;

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        model.addAttribute("user", new UserRequestDTO.UpdateDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registrationForm(@ModelAttribute("user") @Valid UserRequestDTO.UpdateDTO request,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> redirectAttributes.addFlashAttribute(error.getDefaultMessage(), error));
            return "register";
        }

        try {
            User newUser = userCommandService.addUser(request);
            redirectAttributes.addAttribute("userId", newUser.getId());
            log.info("회원가입완료");
            return "redirect:/user/update/{userId}";
        } catch (Exception e) {
            log.error("회원가입 중 문제 발생");
            e.printStackTrace();
            return "register";
        }
    }


    @PostMapping("/update/{userId}")
    public String updateForm(@PathVariable("userId") Long userId,
                                    @ModelAttribute("user") @Valid UserRequestDTO.UpdateDTO request,
                                    BindingResult bindingResult,
                                    Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userId);
            return "update";
        }
        try {
            request.setId(userId);
            userCommandService.updateUser(userDetails,request);
            log.info("사용자 업데이트 완료");
            return "redirect:/user/home";
        } catch (TempHandler e) {
            log.error("사용자 업데이트 실패:{}",e.getErrorReasonHttpStatus());
            e.getErrorReasonHttpStatus();
            return "update";
        }
    }

    @GetMapping("/update/{userId}")
    public String showUpdateForm(@PathVariable("userId") Long userId, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        Long id = Long.valueOf(userDetails.getUsername());

        try {
            if (id.equals(userId)) {
                UserResponseDTO.UpdateResultDTO userDTO = UserUpdateConverter.toUpdateResultDTO(userCommandService.getUserById(userId));
                model.addAttribute("user", userDTO);
                log.info("접근권한 있는 사용자");
                return "update";
            } else {
                model.addAttribute("user", new UserRequestDTO.UpdateDTO());
                log.error("접근권한 없는 사용자");
                throw new RuntimeException("접근권한없음");
            //    return "home";
            }
        }catch (RuntimeException e) {
            log.error("접근권한 확인 중 에러발생:{}",e.getMessage());
            model.addAttribute("user", "User not found");
        }
        return "home";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserRequestDTO.UpdateDTO());
        return "login";
    }


    @PostMapping("/login")
    public String loginForm(@ModelAttribute @Valid UserRequestDTO.UpdateDTO request, HttpSession httpSession) {
    Long id = userCommandService.loginCheck(request, httpSession);

    if (id != null) {
        log.info("로그인한 사용자:{}",request.getId());
        return "redirect:/user/update/" + id;
    } else {
        log.error("존재하지않는 사용자: {}", request.getId());
        return "redirect:/user/login?error=true";

    }
    }

    @GetMapping("/home")
    public String showHome() {
        return "home";
    }

    @PostMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes, HttpSession httpSession) {
        httpSession.invalidate();
        log.info("로그아웃 됨");
        return "redirect:home" ;
    }



    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId,RedirectAttributes redirectAttributes,HttpSession httpSession) {
        userCommandService.deletedById(userId);
        httpSession.invalidate();
        log.info("탈퇴처리완료");
        return "redirect:/user/home"; //절대경로
    }

}
*/