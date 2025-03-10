package renewal.awesome_travel_backoffice.admin;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final AdminDataSourceConfig AdminDataSourceConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("admin 테이블 조회");
        
        Map<String, Object> adminMap = AdminDataSourceConfig.externalDataSource().queryForMap("SELECT * FROM admin WHERE id = ?", username);
        Admin admin = new Admin();
        admin.setId((String) adminMap.get("id"));
        admin.setPassword((String) adminMap.get("password"));

        // Admin admin = adminRepository.findById(username);
        if (admin.getId() == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return User.builder()
                .username(admin.getId())
                .password(passwordEncoder.encode(admin.getPassword())) // 비밀번호 인코딩
                .build();
    }
}
