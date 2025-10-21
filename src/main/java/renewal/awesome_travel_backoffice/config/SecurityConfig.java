package renewal.awesome_travel_backoffice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.admin.AdminService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true) // secure 어노테이션을 활성화, preAuthorize 어노테이션을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1) AuthenticationManagerBuilder 가져와서 UserDetailsService + PasswordEncoder 등록
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(adminService)
                .passwordEncoder(passwordEncoder);
        AuthenticationManager authManager = authBuilder.build();

        // 2) HttpSecurity에 AuthenticationManager 설정
        http
                .authenticationManager(authManager)
                .csrf(csrf -> csrf.disable()) // 개발 중 비활성화, 운영 시 검토
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/admin","/icon/**","/favicon.ico").permitAll()
                        // .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}