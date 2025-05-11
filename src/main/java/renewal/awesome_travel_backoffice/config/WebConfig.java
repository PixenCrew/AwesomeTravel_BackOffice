package renewal.awesome_travel_backoffice.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Value("${imageLocation}")
    private String uploadDir;

    // @Override
    // public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
    // registry.addResourceHandler("/images/**")
    // .addResourceLocations("file:" + new File(uploadDir).getAbsolutePath() + "/");
    // }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String currentWorkingDir = System.getProperty("user.dir");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + currentWorkingDir + "/images/");
    }
}
