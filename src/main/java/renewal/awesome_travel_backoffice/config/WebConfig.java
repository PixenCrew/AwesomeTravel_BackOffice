package renewal.awesome_travel_backoffice.config;

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
        String fullPath = "file:" + uploadDir + "/";
        // System.out.println("fullPath : "+fullPath);
        registry.addResourceHandler("/images/**")
                .addResourceLocations(fullPath)
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());

        // 항공사 아이콘 (공용)
        String iconPath = "file:" + iconDir + "/";
        registry.addResourceHandler("/icon/**")
                .addResourceLocations(iconPath)
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());
    }
}
