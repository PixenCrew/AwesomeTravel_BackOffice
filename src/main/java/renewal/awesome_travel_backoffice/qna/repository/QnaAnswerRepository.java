package renewal.awesome_travel_backoffice.qna.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import renewal.awesome_travel_backoffice.qna.entity.QnaAnswer;

import java.util.List;

public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {
    List<QnaAnswer> findByQnaId(Long qnaId);
}

