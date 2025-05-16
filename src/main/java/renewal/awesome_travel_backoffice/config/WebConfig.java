package renewal.awesome_travel_backoffice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${imageLocation}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String fullPath = "file:" + uploadDir + "/";
        // System.out.println("fullPath : "+fullPath);
        registry.addResourceHandler("/images/**")
                .addResourceLocations(fullPath);
    }
}
