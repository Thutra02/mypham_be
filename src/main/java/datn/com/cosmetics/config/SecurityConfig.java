package datn.com.cosmetics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import datn.com.cosmetics.config.auth.JWTAuthEntryPoint;
import datn.com.cosmetics.config.auth.JWTAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthFilter jwtRequestFilter;
    private final JWTAuthEntryPoint authEntryPoint;

    // Constructor for initializing dependencies
    public SecurityConfig(JWTAuthEntryPoint authEntryPoint,
            JWTAuthFilter jwtRequestFilter) {

        this.authEntryPoint = authEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // Configures HTTP security for the application
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho REST API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không
                                                                                                              // dùng
                                                                                                              // session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Chỉ ADMIN truy cập
                        .anyRequest().authenticated() // Các request khác cần xác thực
                )

                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Thêm CORS
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authEntryPoint))
                .addFilterBefore(jwtRequestFilter, BasicAuthenticationFilter.class)

                .build(); // Build the security configuration
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // Cho phép tất cả origin (có thể giới hạn nếu cần)
        configuration.addAllowedMethod("*"); // Cho phép tất cả method (GET, POST, v.v.)
        configuration.addAllowedHeader("*"); // Cho phép tất cả header
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Exposes the AuthenticationManager bean for authentication purposes
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Defines the PasswordEncoder bean using BCrypt hashing for secure password
    // storage
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpFirewall allowDoubleSlashFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowBackSlash(true); // Cho phép dấu `\`
        firewall.setAllowUrlEncodedSlash(true); // Cho phép mã hóa `/`
        firewall.setAllowSemicolon(true); // Nếu URL có dấu `;`
        return firewall;
    }
}