package renewal.awesome_travel_backoffice.image.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename; // 원본 파일명

    @Column(nullable = false)
    private String mimeType; // 파일 MIME 타입

    @Column(nullable = false)
    private Long fileSize; // 파일 크기 (bytes)

    @Column(nullable = false)
    private String uploadType; // "DRIVE" or "LOCAL"

    // Google Drive 관련 필드
    @Column(length = 500)
    private String driveFileId; // Google Drive 파일 ID

    @Column(length = 1000)
    private String driveWebViewLink; // Google Drive 뷰어 페이지 링크

    @Column(length = 1000)
    private String driveImageUrl; // 이미지 직접 표시용 URL (<img> 태그에서 사용) ⭐

    @Column(length = 1000)
    private String driveDownloadLink; // Google Drive 다운로드 링크

    // 로컬 파일 관련 필드
    @Column(length = 1000)
    private String filePath; // 로컬 파일 경로

    @Column(nullable = false)
    private LocalDateTime createdAt; // 업로드 시간

    // 팩토리 메서드: Google Drive 업로드
    public static UploadedFile createDriveFile(
            String originalFilename,
            String mimeType,
            Long fileSize,
            String driveFileId,
            String driveWebViewLink,
            String driveImageUrl,
            String driveDownloadLink) {
        UploadedFile file = new UploadedFile();
        file.originalFilename = originalFilename;
        file.mimeType = mimeType;
        file.fileSize = fileSize;
        file.uploadType = "DRIVE";
        file.driveFileId = driveFileId;
        file.driveWebViewLink = driveWebViewLink;
        file.driveImageUrl = driveImageUrl;  // 이미지 직접 표시용 URL
        file.driveDownloadLink = driveDownloadLink;
        file.createdAt = LocalDateTime.now();
        return file;
    }

    // 팩토리 메서드: 로컬 파일 업로드
    public static UploadedFile createLocalFile(
            String originalFilename,
            String mimeType,
            Long fileSize,
            String filePath) {
        UploadedFile file = new UploadedFile();
        file.originalFilename = originalFilename;
        file.mimeType = mimeType;
        file.fileSize = fileSize;
        file.uploadType = "LOCAL";
        file.filePath = filePath;
        file.createdAt = LocalDateTime.now();
        return file;
    }
}


