package journeybuddy.spring.config.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtUtil jwtUtil;

    //실제 필터링 로직
    //토큰의 인증정보를 Security Context에 저장하는 역할
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if(StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
            String userEmail = jwtUtil.getUserEmail(jwt);
            String role = jwtUtil.getRole(jwt);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail,null,null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("인증정보저장에 성공:{}",authentication);
        }
        else{
            log.info("유효한 토큰이 존재하지 않음:{}",requestURI);
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
