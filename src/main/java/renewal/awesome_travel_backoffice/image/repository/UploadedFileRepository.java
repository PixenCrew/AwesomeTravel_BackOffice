package renewal.awesome_travel_backoffice.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import renewal.awesome_travel_backoffice.image.entity.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    
    /**
     * 생성일시 기준 내림차순 정렬된 전체 파일 목록 조회
     */
    List<UploadedFile> findAllByOrderByCreatedAtDesc();
    
    /**
     * 업로드 타입으로 정렬된 파일 목록 조회
     */
    List<UploadedFile> findByUploadTypeOrderByCreatedAtDesc(String uploadType);
}

