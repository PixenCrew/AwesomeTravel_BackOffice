package renewal.awesome_travel_backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {
		"renewal.common",
		"renewal.awesome_travel_backoffice"
})
@EntityScan(basePackages = {
		"renewal.common",
		"renewal.awesome_travel_backoffice"
})
@EnableJpaRepositories(basePackages = {
		"renewal.common",
		"renewal.awesome_travel_backoffice"
})
public class AwesomeTravelBackOffice {

	public static void main(String[] args) {
		SpringApplication.run(AwesomeTravelBackOffice.class, args);
	}

}
