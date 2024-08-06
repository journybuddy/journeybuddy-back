package journeybuddy.spring.config.OAuth2;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import journeybuddy.spring.domain.Role;
import journeybuddy.spring.domain.User;
import journeybuddy.spring.repository.RoleRepository;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class KaKaoService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final String clientId;

    @Autowired
    public KaKaoService(UserRepository userRepository, RoleRepository roleRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.clientId = appConfig.getKakaoClientId();
    }

    public String getToken(String code) throws Exception {
        String access_Token = "";
        final String requestUrl = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(requestUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        StringBuilder sb = new StringBuilder();
        sb.append("grant_type=authorization_code");
        sb.append("&client_id=").append(clientId);
        sb.append("&redirect_uri=http://localhost:3000/journeybuddy/oauth");
        sb.append("&code=").append(code);
        bw.write(sb.toString());
        bw.flush();

        log.info("Requesting token with parameters: {}", sb.toString());

        // 응답 코드 확인
        int responseCode = con.getResponseCode();
        log.info("Response Code: {}", responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            log.info("Token response: {}", result.toString());

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result.toString());
            access_Token = element.getAsJsonObject().get("access_token").getAsString();

            br.close();
            bw.close();
        } else {
            throw new RuntimeException("Failed to get access token: " + responseCode);
        }

        return access_Token;
    }

    public User getUserInfo(String accessToken) throws Exception {
        final String requestUrl = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = bf.readLine()) != null) {
            response.append(line);
        }

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(response.toString());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject properties = jsonObject.has("properties") ? jsonObject.getAsJsonObject("properties") : new JsonObject();
        JsonObject kakaoAccount = jsonObject.has("kakao_account") ? jsonObject.getAsJsonObject("kakao_account") : new JsonObject();

        String thumbnailImage = properties.has("thumbnail_image") ? properties.get("thumbnail_image").getAsString() : null;
        String nickname = properties.has("nickname") ? properties.get("nickname").getAsString() : null;
        String email = kakaoAccount.has("email") ? kakaoAccount.get("email").getAsString() : null;
        String birthday = kakaoAccount.has("birthday") ? kakaoAccount.get("birthday").getAsString() : null;

        log.info("nickname:{}", nickname);
        log.info("email:{}", email);

        // DB에서 사용자 조회 및 저장
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        // role 권한 부여 추가 (현재는 role 공백이어도 로그인 가능하게 처리해 놓음)
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = Role.builder().name("USER").build();
            roleRepository.save(userRole);
        }

        if (!existingUser.isPresent()) {
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));

            user = User.builder()
                    .nickname(nickname)
                    .email(email)
                    .roles(Collections.singletonList(defaultRole))
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get();
        }
        return user;
    }
}
