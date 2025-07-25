package renewal.awesome_travel_backoffice.config;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class AuditingFields {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; //생성일시

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy; //생성자

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime modifiedAt; //수정일시

    @LastModifiedBy
    @Column(nullable = true, length = 100)
    private String modifiedBy;
}
