package journeybuddy.spring.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import journeybuddy.spring.apiPayload.ApiResponse;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.config.OAuth2.KaKaoService;
import journeybuddy.spring.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class KaKaoRestController {


    private final JwtUtil jwtUtil;
    private final KaKaoService kakaoService;
    private final ObjectMapper objectMapper;


    @RequestMapping(value = "/kakao")
    public ResponseEntity<Map<String,Object>> kakaoLogin(@RequestParam("code") String code) throws Exception {

        String accessToken = kakaoService.getToken(code);
        User userInfo = kakaoService.getUserInfo(accessToken);

        // JWT 토큰 생성
        String jwtToken = jwtUtil.generateOAuth2Token(userInfo.getEmail());

        log.info("code: {}", code);
        log.info("로그인된 사용자: {}", userInfo.getEmail());
        log.info("jwtToken: {}", jwtToken);
        log.info("UserInfo: {}", userInfo.getEmail());
        log.info("UserInfo: {}", userInfo.getNickname());

        // User 객체를 JSON 문자열로 변환
        String userInfoJson = objectMapper.writeValueAsString(userInfo);

        log.info("accessToken:{}",accessToken);
        log.info("code: {}", code);
        log.info("로그인된 사용자 이메일: {}", userInfo.getEmail());
        log.info("jwtToken: {}", jwtToken);
        log.info("UserInfo JSON: {}", userInfoJson);

        Map<String,Object> jwtAndInfo = new HashMap<>();
        jwtAndInfo.put("userInfo",userInfo);
        jwtAndInfo.put("jwt",jwtToken);

        // JWT 토큰을 반환
        return ResponseEntity.ok(jwtAndInfo);
    }

    @GetMapping("/kakao/info")
    public ResponseEntity<String> kakaoLoginInfo(@AuthenticationPrincipal UserDetails userDetails) throws Exception {

        try {
            // code로 토큰 받음
            String userEmail = userDetails.getUsername();
            log.info("User: {}", userEmail);
            String userInfoJson = objectMapper.writeValueAsString(userDetails);
            log.info("UserInfo JSON: {}", userInfoJson);

            // User 객체를 JSON 응답으로 반환
            return new ResponseEntity<>(userInfoJson, HttpStatus.OK);
        } catch (Exception e) {
            // 예외 처리
            log.error("Error in kakaoLoginInfo", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
