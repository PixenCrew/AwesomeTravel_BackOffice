package renewal.awesome_travel_backoffice.inquiry.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.inquiry.entity.InquiryAnswer;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
    Optional<InquiryAnswer> findByInquiryId(Long inquiryId);
}
