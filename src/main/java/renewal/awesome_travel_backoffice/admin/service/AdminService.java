package renewal.awesome_travel_backoffice.admin.service;

import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.admin.entity.Admin;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate adminJdbcTemplate;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("admin 테이블 조회");

        try {
            Map<String, Object> adminMap = adminJdbcTemplate.queryForMap("SELECT * FROM admin WHERE id = ?", username);
        return User.builder()
                .username(adminMap.get("id").toString())
                .password(adminMap.get("password").toString()) // 인코딩된 비밀번호 사용
                .roles(adminMap.get("role").toString())
                .build();

        } catch (EmptyResultDataAccessException e) {
            // 유저가 존재하지 않으면
            throw new UsernameNotFoundException("User not found with username: " + username, e);
        } catch (IncorrectResultSizeDataAccessException e) {
            // 결과가 2개 이상인 경우
            throw new IllegalStateException("Multiple users found with username: " + username, e);
        }
    }

    public void createUser(Admin admin) {

        String hashedPassword = passwordEncoder.encode(admin.getPassword()); // 인코딩된 비밀번호 사용
        String sql = "INSERT INTO admin (id, password, role) VALUES (?, ?, ?)";

        adminJdbcTemplate.update(sql, admin.getId(), hashedPassword, admin.getRole().name());

    }
}