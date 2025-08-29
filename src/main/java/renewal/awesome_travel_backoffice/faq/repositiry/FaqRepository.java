package renewal.awesome_travel_backoffice.faq.repositiry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.faq.utils.FaqCategory;
import renewal.common.entity.Faq;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    Page<Faq> findByCategory(FaqCategory category, Pageable pageable);

}
