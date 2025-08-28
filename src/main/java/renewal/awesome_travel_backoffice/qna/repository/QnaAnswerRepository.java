package renewal.awesome_travel_backoffice.qna.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.qna.entity.QnaAnswer;

public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {
    List<QnaAnswer> findByQnaId(Long qnaId);
}

