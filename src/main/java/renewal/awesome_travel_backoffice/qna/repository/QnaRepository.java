package renewal.awesome_travel_backoffice.qna.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import renewal.awesome_travel_backoffice.qna.entity.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long> {
    Page<Qna> findByIsAnswered(boolean isAnswered, Pageable pageable);

    @Query("SELECT q FROM Qna q WHERE " +
            "( :keyword IS NULL OR q.title LIKE %:keyword% OR q.content LIKE %:keyword% ) AND " +
            "( :isAnswered IS NULL OR q.isAnswered = :isAnswered )")
    Page<Qna> searchAdmin(@Param("keyword") String keyword,
                          @Param("isAnswered") Boolean isAnswered,
                          Pageable pageable);

}

