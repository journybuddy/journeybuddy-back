package journeybuddy.spring.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import journeybuddy.spring.config.JWT.CustomUserDetails;
import journeybuddy.spring.config.JWT.JwtFilter;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.config.OAuth2.KaKaoService;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.web.dto.UserDTO.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
public class KaKaoRestController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final KaKaoService kakaoService;


    //백엔드에서 인가코드 확인용코드 임시 url, getmapping에 리다이렉트코드 입력해야함

    @RequestMapping(value = "/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code, HttpSession session) throws Exception {
        String accessToken = kakaoService.getToken(code);
        User userInfo = kakaoService.getUserInfo(accessToken);

        // JWT 토큰 생성
        String jwtToken = jwtUtil.generateOAuth2Token(userInfo.getEmail());

        return ResponseEntity.ok(jwtToken); // JWT 토큰을 반환
    }


    @RequestMapping("/kakao/info")
    public ResponseEntity<User> kakaoLogin(@RequestParam("code") String code) throws Exception {

        // code로 토큰 받음
        String accessToken = kakaoService.getToken(code);

        // 토큰으로 사용자 정보 가져오기
        User user = kakaoService.getUserInfo(accessToken);

        // User 객체를 JSON 응답으로 반환
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
/*
    @GetMapping("/user-info")
    public String getUserInfo(@RequestParam("accessToken") String accessToken) {
        kakaoService.getUserInfo(accessToken);
        // 예시로, 실제 애플리케이션에서는 필요한 정보를 가공해서 반환
        return "User info fetched successfully!";
    }
}*/

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