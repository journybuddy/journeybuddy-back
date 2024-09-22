package journeybuddy.spring.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@AllArgsConstructor
public class RandomUtils {

    private static final String UPPER_CASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String LOWER_CASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";


    public static String generateRandomURL(int length, boolean isUpperCase) {
        String characters = isUpperCase ? UPPER_CASE_CHARACTERS : LOWER_CASE_CHARACTERS;
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(characters.length());
            stringBuilder.append(characters.charAt(number));
        }
        return stringBuilder.toString();
    }
}