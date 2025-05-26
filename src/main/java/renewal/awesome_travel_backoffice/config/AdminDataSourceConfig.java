package renewal.awesome_travel_backoffice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AdminDataSourceConfig {

    @Bean("adminJdbcTemplate")
    public JdbcTemplate externalDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mariadb://127.0.0.1:3306/admin");
        dataSource.setUsername("awesomebackoffice");
        dataSource.setPassword("travelbackoffice");
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        return new JdbcTemplate(dataSource);
    }

}