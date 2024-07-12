package journeybuddy.spring.config;

import journeybuddy.spring.config.Handler.EntryPointDeniedHandler;
import journeybuddy.spring.config.Handler.MyLoginFailureHandler;
import journeybuddy.spring.config.Handler.MyLoginSuccessHandler;
import journeybuddy.spring.config.JWT.JwtAccessDeniedHandler;
import journeybuddy.spring.config.JWT.JwtAuthenticationEntryPoint;
import journeybuddy.spring.repository.UserRepository;
import journeybuddy.spring.service.UserService.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public DelegatingSecurityContextRepository delegatingSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");  // 스웨거 UI의 호스트 서버 주소를 지정해야 할 수 있습니다.
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/login/*").permitAll()
                        .requestMatchers("/user/delete/*").permitAll()
                        .requestMatchers("/user/update/*").permitAll()
                                .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/", "/user/login", "/user/register").permitAll()
                        .requestMatchers("/", "/api*", "/api-docs/**", "/swagger-ui/**","/v3/**").permitAll()
                                .anyRequest().permitAll()
                        //        .anyRequest().authenticated()
                )
/*                .formLogin(formLogin -> formLogin
                        .loginPage("/user/login")
                        .permitAll()
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(new MyLoginSuccessHandler())
                )*/
                .exceptionHandling(exception->exception
                                .accessDeniedHandler(new JwtAccessDeniedHandler())
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )
                .securityContext((securityContext) -> {
        //            securityContext.securityContextRepository(delegatingSecurityContextRepository());
                    securityContext.requireExplicitSave(true);
                })
                .logout(logout -> logout
                        .logoutUrl("/logout").permitAll()
                        .logoutSuccessUrl("/").permitAll()
                        .invalidateHttpSession(true)
                        .permitAll()
                );
        return http.build();
    }
}