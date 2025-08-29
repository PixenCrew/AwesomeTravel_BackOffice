package renewal.awesome_travel_backoffice.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.common.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}
