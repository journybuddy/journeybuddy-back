package journeybuddy.spring.config.OAuth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import journeybuddy.spring.config.JWT.CustomUserDetails;
import journeybuddy.spring.config.JWT.JwtUtil;
import journeybuddy.spring.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("OAuth2 authentication successful");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        CustomUserDetails customUserDetail = (CustomUserDetails) authentication.getPrincipal();

        log.info("Principal에서 꺼낸 OAuth2User = {}", customUserDetail);


        // 카카오 계정 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        Map<String, Object> profile = (Map<String, Object>) oAuth2User.getAttributes().get("profile");
        String nickname = (String) profile.get("nickname");

        // 클레임 생성
        Map<String, Object> claims = Map.of(
                "email", email,
                "nickname", nickname
        );

        log.info("토큰 발행 시작");
        String token = jwtUtil.generateOAuthToken(claims);
        log.info("{}", token);

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/login/access")
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
}

