package journeybuddy.spring.config.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
        private final UserDetailsService userDetailsService;
        private SecretKey secretKey; //JWT 토큰 객체 키를 저장할 시크릿 키
        private final long accessTokenExpTime; //JWT 토큰 만료시간
        private static final long refreshTokenExpTime = 1000 * 60 * 60 * 24 * 7;

    public JwtUtil(@org.springframework.beans.factory.annotation.Value("${spring.jwt.secretkey}") String secretKey, UserDetailsService userDetailsService,
                   @Value("${spring.jwt.expiration_time}") long accessTokenExpTime) {
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            this.accessTokenExpTime = accessTokenExpTime;
        }

    public String extractUserEmail(String token) {
        return extractAllFromToken(token, Claims::getSubject);
    }

    public <T> T extractAllFromToken(String token, Function<Claims, T> claimsResolver) {
        log.info("Parsing JWT Token: {}", token);
        Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    //oauth2발급받는 토큰
    public String generateOAuth2Token(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpTime))
                .signWith(secretKey)
                .compact();
    }



    public String generateToken2(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpTime))
                .signWith(secretKey)
                .compact();
    }


    /*
    //일반로그인 발급 토큰
    public String generateToken(Map<String, Object> claims, User user) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpTime))
                .signWith(secretKey)
                .compact();
    }

    //인증후 accessToken발급받음
    public String createAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        return generateToken(Collections.emptyMap(), user);
    }

     */
    //토큰인증
    public Authentication getAuthentication(String token) {
        String userEmail = extractUserEmail(token);
        log.info("Extracted user email from token: {}", userEmail);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    public boolean isExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }



    public String createRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpTime))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.info("Invalid JWT Token", e);
        }
        return false;
    }
}