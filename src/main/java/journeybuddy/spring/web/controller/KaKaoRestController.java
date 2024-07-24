package journeybuddy.spring.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import journeybuddy.spring.config.JWT.CustomUserDetails;
import journeybuddy.spring.config.JWT.JwtFilter;
import journeybuddy.spring.config.JWT.JwtUtil;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
public class KaKaoRestController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;


    //백엔드에서 인가코드 확인용코드 임시 url, getmapping에 리다이렉트코드 입력해야함
    @GetMapping(value = "login/access")
    public ResponseEntity<TokenDTO> oauth2Authenticate(HttpServletRequest request) {
        // OAuth2 인증이 완료되었을 때, SecurityContextHolder에 저장된 Authentication 객체를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자가 없는 경우, 401 Unauthorized를 반환합니다.
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenDTO("Error: Unauthorized"));
        }

        // JWT 토큰을 생성합니다.
        String jwt = jwtUtil.createAccessToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // TokenDTO를 이용해 response body에 JWT를 넣어서 반환합니다.
        return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK);
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