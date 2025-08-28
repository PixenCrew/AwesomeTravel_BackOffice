package renewal.awesome_travel_backoffice.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class JpaConfig {
	
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        //Spring Security의 Authentication을 가져와서 사용자명 반환
    }
}
