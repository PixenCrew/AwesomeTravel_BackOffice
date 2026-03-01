package renewal.awesome_travel_backoffice.image.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.image.entity.UploadedFile;
import renewal.awesome_travel_backoffice.image.repository.UploadedFileRepository;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    private final UploadedFileRepository uploadedFileRepository;
    private final GoogleDriveService googleDriveService;
    private final FileService localFileService;
    private final DriveFileCleanupService driveFileCleanupService;

    @Value("${file.upload.type:DRIVE}")
    private String uploadType; // "DRIVE" or "LOCAL"

    /**
     * 파일 업로드 (Drive 또는 Local)
     * @param multipartFile 업로드할 파일
     * @param folderType 폴더 타입 (product, notice, banner, popup, promotion, hotel, excel 등)
     */
    @Transactional
    public UploadedFile uploadFile(MultipartFile multipartFile, String folderType) throws Exception {
        if ("DRIVE".equalsIgnoreCase(uploadType)) {
            return uploadToDrive(multipartFile, folderType);
        } else {
            return uploadToLocal(multipartFile);
        }
    }

    /**
     * 파일 업로드 (Drive 또는 Local) - 기본 폴더 사용
     */
    @Transactional
    public UploadedFile uploadFile(MultipartFile multipartFile) throws Exception {
        return uploadFile(multipartFile, null);
    }

    /**
     * Google Drive에 업로드
     */
    private UploadedFile uploadToDrive(MultipartFile multipartFile, String folderType) throws Exception {
        String folderId = getFolderIdByType(folderType);
        GoogleDriveService.DriveUploadResult result = googleDriveService.uploadFile(multipartFile, folderId);

        UploadedFile uploadedFile = UploadedFile.createDriveFile(
                multipartFile.getOriginalFilename(),
                result.getMimeType(),
                result.getFileSize(),
                result.getFileId(),
                result.getWebViewLink(),
                result.getImageUrl(),  // 이미지 직접 표시용 URL
                result.getDownloadLink()
        );

        return uploadedFileRepository.save(uploadedFile);
    }

    /**
     * 로컬에 업로드
     */
    private UploadedFile uploadToLocal(MultipartFile multipartFile) throws Exception {
        String savedFileName = localFileService.uploadFile(multipartFile);
        String filePath = "/images/" + savedFileName;

        String mimeType = multipartFile.getContentType();
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }
        long size = multipartFile.getSize();
        Long fileSize = size >= 0 ? size : 0L;

        UploadedFile uploadedFile = UploadedFile.createLocalFile(
                multipartFile.getOriginalFilename() != null ? multipartFile.getOriginalFilename() : savedFileName,
                mimeType,
                fileSize,
                filePath
        );

        return uploadedFileRepository.save(uploadedFile);
    }

    /**
     * 파일 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UploadedFile> listFiles() {
        return uploadedFileRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 파일 상세 조회
     */
    @Transactional(readOnly = true)
    public UploadedFile getFile(Long fileId) {
        return uploadedFileRepository.findById(fileId)
                .orElse(null);
    }

    /**
     * 파일 삭제
     */
    @Transactional
    public boolean deleteFile(Long fileId) throws Exception {
        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElse(null);

        if (file == null) {
            return false;
        }

        // Google Drive 파일인 경우 Drive에서도 삭제
        if ("DRIVE".equals(file.getUploadType()) && file.getDriveFileId() != null) {
            try {
                googleDriveService.deleteFile(file.getDriveFileId());
            } catch (Exception e) {
                log.warn("Google Drive 파일 삭제 실패: {}", e.getMessage());
            }
        }
        // 로컬 파일인 경우 로컬에서도 삭제
        else if ("LOCAL".equals(file.getUploadType()) && file.getFilePath() != null) {
            try {
                localFileService.deleteFile(file.getFilePath());
            } catch (Exception e) {
                log.warn("로컬 파일 삭제 실패: {}", e.getMessage());
            }
        }

        uploadedFileRepository.delete(file);
        return true;
    }

    /**
     * 폴더 타입에 따라 폴더 ID 반환
     */
    @Value("${google.drive.folder.id.product:}")
    private String productFolderId;
    
    @Value("${google.drive.folder.id.notice:}")
    private String noticeFolderId;
    
    @Value("${google.drive.folder.id.banner:}")
    private String bannerFolderId;
    
    @Value("${google.drive.folder.id.popup:}")
    private String popupFolderId;
    
    @Value("${google.drive.folder.id.promotion:}")
    private String promotionFolderId;
    
    @Value("${google.drive.folder.id.hotel:}")
    private String hotelFolderId;
    
    @Value("${google.drive.folder.id.excel:}")
    private String excelFolderId;

    private String getFolderIdByType(String folderType) {
        if (folderType == null || folderType.isEmpty()) {
            return null; // 기본 폴더 사용
        }

        return switch (folderType.toLowerCase()) {
            case "product" -> productFolderId;
            case "notice", "notice.image", "notice_image" -> noticeFolderId;
            case "banner" -> bannerFolderId;
            case "popup" -> popupFolderId;
            case "promotion" -> promotionFolderId;
            case "hotel" -> hotelFolderId;
            case "excel" -> excelFolderId;
            default -> null; // 알 수 없는 타입은 기본 폴더 사용
        };
    }

    /**
     * 깨진 이미지 URL 정리 (Google Drive에서 수동으로 삭제된 파일의 DB 레코드 정리)
     * @return 정리된 항목 수
     */
    @Transactional
    public int cleanupOrphanedFiles() {
        // DriveFileCleanupService에 위임
        return driveFileCleanupService.cleanupOrphanedFiles();
    }
}

