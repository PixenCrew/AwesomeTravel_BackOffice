package renewal.awesome_travel_backoffice.admin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository  extends JpaRepository<Admin, Long> {
    Admin findById(String username);
}