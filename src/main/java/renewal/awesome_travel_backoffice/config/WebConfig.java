package renewal.awesome_travel_backoffice.config;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${imageLocation}")
    private String uploadDir;

    @Value("${iconLocation}")
    private String iconDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(asResourceLocation(uploadDir))
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());

        // 항공사 아이콘 (공용)
        registry.addResourceHandler("/icon/**")
                .addResourceLocations(asResourceLocation(iconDir))
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());
    }

    private String asResourceLocation(String location) {
        // classpath 접두라면 그대로 사용 (Spring이 내부 리소스를 처리함)
        if (location.startsWith("classpath:")) {
            return location.endsWith("/") ? location : location + "/";
        }
        // 그 외에는 절대경로 file URI로 변환
        return Paths.get(location).toAbsolutePath().normalize().toUri().toString();
    }
}
