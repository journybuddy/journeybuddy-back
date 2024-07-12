package journeybuddy.spring.config.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import journeybuddy.spring.converter.UserUpdateConverter;
import journeybuddy.spring.domain.RefreshToken;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.RefreshTokenRepository;
import journeybuddy.spring.service.UserService.CustomUserDetails;
import journeybuddy.spring.web.dto.UserDTO.UserRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

        private SecretKey secretKey; //JWT 토큰 객체 키를 저장할 시크릿 키
        private final long accessTokenExpTime; //JWT 토큰 만료시간
        private static final long refreshTokenExpTime = 1000 * 60 * 60 * 24 * 7;

    public JwtUtil(@org.springframework.beans.factory.annotation.Value("${spring.jwt.secretkey}") String secretKey,
                       @Value("${spring.jwt.expiration_time}") long accessTokenExpTime) {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            this.accessTokenExpTime = accessTokenExpTime;
        }

        public String getUserEmail(String token) {
            log.debug("Parsing JWT Token: {}", token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);

        }

        public String getRole(String token) {

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
/*
    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getAllClaimsFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
*/
    public boolean isExpired(String token) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        }

        //accessToken 생성
        public String createAccessToken(Authentication authentication) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = customUserDetails.getUser();

            return createToken(user.getEmail(), accessTokenExpTime);
        }

        //refreshToken 생성후 DB에 저장해야함
        public String createRefreshToken(){

            return Jwts.builder()
                    .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpTime))
                    .signWith(secretKey)
                    .compact();
        }


        //JWT 생성
        public String createToken(String email,Long expiredMs) {

            return Jwts.builder()
                    .claim("email", email)
                    //        .claim("role", role)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                    .signWith(secretKey)
                    .compact();
        }

        //Jwt검증
        public boolean validateToken(String token) {
            try {
                Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
                return true;
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                log.info("Invalid JWT Token", e);
            } catch (ExpiredJwtException e) {
                log.info("Expired JWT Token", e);
            } catch (UnsupportedJwtException e) {
                log.info("Unsupported JWT Token", e);
            } catch (IllegalArgumentException e) {
                log.info("JWT claims string is empty.", e);
            }
            return false;
        }

        //JWT Claim 추출
        public Claims parseClaims(String accessToken) {
            try {
                return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
            } catch (ExpiredJwtException e) {
                return e.getClaims();
            }
        }
    }