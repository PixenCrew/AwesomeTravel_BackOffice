package renewal.awesome_travel_backoffice.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import renewal.awesome_travel_backoffice.notification.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}
