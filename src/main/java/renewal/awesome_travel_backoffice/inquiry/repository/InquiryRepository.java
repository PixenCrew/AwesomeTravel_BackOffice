package renewal.awesome_travel_backoffice.inquiry.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.common.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("SELECT i FROM Inquiry i WHERE " +
            "(:keyword IS NULL OR i.title LIKE %:keyword% OR i.content LIKE %:keyword%) AND " +
            "(:isAnswered IS NULL OR i.isAnswered = :isAnswered)")
    Page<Inquiry> searchAdmin(@Param("keyword") String keyword,
                              @Param("isAnswered") Boolean isAnswered,
                              Pageable pageable);
}
