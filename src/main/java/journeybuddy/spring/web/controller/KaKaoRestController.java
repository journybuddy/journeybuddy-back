package journeybuddy.spring.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
public class KaKaoRestController {

    //백엔드에서 인가코드 확인용코드 임시 url, getmapping에 리다이렉트코드 입력해야함
    @GetMapping(value = "login/access")
    public String kakaoOauthRedirect(@RequestParam String code) {
        return "카카오 로그인 인증 완료, code : " + code;
    }
}

    /*
    private final KaKaoCommandServiceImpl kaKaoCommandServiceImpl;


    @PostMapping("/login123")
    public ApiResponse<String> loginWithKakao(@RequestParam String accessToken) {
        String jwtToken = kaKaoCommandServiceImpl.loginByKakao(accessToken);
        log.info("User logged in successfully with JWT: " + jwtToken);
        return ApiResponse.onSuccess(jwtToken);
    }




    @PostMapping("/signup")
    public ApiResponse<User> signupWithKakao(@RequestParam String accessToken) {
        User user = kaKaoCommandServiceImpl.signupByKakao(accessToken);
        log.info("User signed up successfully: " + user.getEmail());
        return ApiResponse.onSuccess(user);
    }

    @PostMapping("/update")
    public ApiResponse<User> updateKaKaoUser(@RequestParam String accessToken, @RequestParam String newNickname) {
        User updatedUser = kaKaoCommandServiceImpl.updateKaKaoUser(accessToken, newNickname);
        log.info("User updated successfully: " + updatedUser.getEmail());
        return ApiResponse.onSuccess(updatedUser);
    }
}

*/