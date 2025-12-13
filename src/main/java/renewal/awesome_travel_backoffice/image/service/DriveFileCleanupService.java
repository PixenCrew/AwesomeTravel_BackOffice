package renewal.awesome_travel_backoffice.image.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import renewal.common.entity.Banner;
import renewal.common.entity.Notice;
import renewal.common.entity.Popup;
import renewal.common.entity.Product;
import renewal.common.entity.Promotion;
import renewal.awesome_travel_backoffice.image.entity.UploadedFile;
import renewal.awesome_travel_backoffice.banner.repository.BannerRepository;
import renewal.awesome_travel_backoffice.notice.repository.NoticeRepository;
import renewal.awesome_travel_backoffice.popup.repository.PopupRepository;
import renewal.common.repository.ProductRepository;
import renewal.common.repository.PromotionRepository;
import renewal.awesome_travel_backoffice.image.repository.UploadedFileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriveFileCleanupService {

    private final GoogleDriveService googleDriveService;
    private final UploadedFileRepository uploadedFileRepository;
    private final ProductRepository productRepository;
    private final BannerRepository bannerRepository;
    private final PopupRepository popupRepository;
    private final PromotionRepository promotionRepository;
    private final NoticeRepository noticeRepository;

    // Google Drive 이미지 URL에서 파일 ID 추출하는 패턴
    private static final Pattern DRIVE_IMAGE_URL_PATTERN = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
    private static final Pattern DRIVE_FILE_ID_PATTERN = Pattern.compile("id=([a-zA-Z0-9_-]+)");

    /**
     * 정기적으로 Google Drive 파일 존재 여부 확인 및 정리
     * 매일 새벽 3시에 실행
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void scheduledCleanup() {
        cleanupOrphanedFiles();
    }

    /**
     * Google Drive 파일 존재 여부 확인 및 정리
     * @return 정리된 항목 수
     */
    @Transactional
    public int cleanupOrphanedFiles() {
        log.info("[파일 정리] 시작");

        int cleanedCount = 0;

        // 1. UploadedFile 테이블 정리
        cleanedCount += cleanupUploadedFiles();

        // 2. Product 엔티티의 이미지 URL 정리
        cleanedCount += cleanupProductImages();

        // 3. Banner 엔티티의 이미지 URL 정리
        cleanedCount += cleanupBannerImages();

        // 4. Popup 엔티티의 이미지 URL 정리
        cleanedCount += cleanupPopupImages();

        // 5. Promotion 엔티티의 이미지 URL 정리
        cleanedCount += cleanupPromotionImages();

        // 6. Notice 엔티티의 이미지 URL 정리
        cleanedCount += cleanupNoticeImages();

        log.info("[파일 정리] 완료 - 총 {}건 정리됨", cleanedCount);
        return cleanedCount;
    }

    /**
     * UploadedFile 테이블에서 삭제된 파일 정리
     */
    private int cleanupUploadedFiles() {
        int count = 0;
        List<UploadedFile> driveFiles = uploadedFileRepository.findAll().stream()
                .filter(file -> "DRIVE".equals(file.getUploadType()))
                .filter(file -> file.getDriveFileId() != null)
                .toList();

        for (UploadedFile file : driveFiles) {
            try {
                // Google Drive에서 파일 존재 여부 확인
                if (!googleDriveService.fileExists(file.getDriveFileId())) {
                    log.warn("[파일 정리] UploadedFile 삭제: ID={}, FileId={}, Filename={}", 
                            file.getId(), file.getDriveFileId(), file.getOriginalFilename());
                    uploadedFileRepository.delete(file);
                    count++;
                }
            } catch (Exception e) {
                log.error("[파일 정리] UploadedFile 확인 실패: ID={}, FileId={}, Error={}", 
                        file.getId(), file.getDriveFileId(), e.getMessage());
            }
        }

        return count;
    }

    /**
     * Product 엔티티의 깨진 이미지 URL 정리
     */
    private int cleanupProductImages() {
        int count = 0;
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            boolean updated = false;

            // thumbnail 정리
            if (product.getThumbnail() != null && !product.getThumbnail().isEmpty()) {
                String fileId = extractFileIdFromUrl(product.getThumbnail());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Product thumbnail 삭제: ProductId={}, URL={}", 
                            product.getId(), product.getThumbnail());
                    product.setThumbnail(null);
                    updated = true;
                }
            }

            // photos 정리
            if (product.getPhotos() != null && !product.getPhotos().isEmpty()) {
                List<String> validPhotos = product.getPhotos().stream()
                        .filter(photo -> {
                            if (photo == null || photo.isEmpty()) return false;
                            String fileId = extractFileIdFromUrl(photo);
                            if (fileId == null) return true; // URL 형식이 아니면 유지
                            return googleDriveService.fileExists(fileId);
                        })
                        .toList();

                if (validPhotos.size() != product.getPhotos().size()) {
                    log.warn("[파일 정리] Product photos 정리: ProductId={}, Before={}, After={}", 
                            product.getId(), product.getPhotos().size(), validPhotos.size());
                    product.setPhotos(validPhotos);
                    updated = true;
                }
            }

            // image 정리
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                String fileId = extractFileIdFromUrl(product.getImage());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Product image 삭제: ProductId={}, URL={}", 
                            product.getId(), product.getImage());
                    product.setImage(null);
                    updated = true;
                }
            }

            if (updated) {
                productRepository.save(product);
                count++;
            }
        }

        return count;
    }

    /**
     * Banner 엔티티의 깨진 이미지 URL 정리
     */
    private int cleanupBannerImages() {
        int count = 0;
        List<Banner> banners = bannerRepository.findAll();

        for (Banner banner : banners) {
            if (banner.getFile() != null && !banner.getFile().isEmpty()) {
                String fileId = extractFileIdFromUrl(banner.getFile());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Banner file 삭제: BannerId={}, URL={}", 
                            banner.getId(), banner.getFile());
                    banner.setFile(null);
                    bannerRepository.save(banner);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Popup 엔티티의 깨진 이미지 URL 정리
     */
    private int cleanupPopupImages() {
        int count = 0;
        List<Popup> popups = popupRepository.findAll();

        for (Popup popup : popups) {
            if (popup.getFile() != null && !popup.getFile().isEmpty()) {
                String fileId = extractFileIdFromUrl(popup.getFile());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Popup file 삭제: PopupId={}, URL={}", 
                            popup.getId(), popup.getFile());
                    popup.setFile(null);
                    popupRepository.save(popup);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Promotion 엔티티의 깨진 이미지 URL 정리
     */
    private int cleanupPromotionImages() {
        int count = 0;
        List<Promotion> promotions = promotionRepository.findAll();

        for (Promotion promotion : promotions) {
            boolean updated = false;

            // thumbnailImg 정리
            if (promotion.getThumnailImg() != null && !promotion.getThumnailImg().isEmpty()) {
                String fileId = extractFileIdFromUrl(promotion.getThumnailImg());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Promotion thumbnailImg 삭제: PromotionId={}, URL={}", 
                            promotion.getId(), promotion.getThumnailImg());
                    promotion.setThumnailImg(null);
                    updated = true;
                }
            }

            // contentImg 정리
            if (promotion.getContentImg() != null && !promotion.getContentImg().isEmpty()) {
                String fileId = extractFileIdFromUrl(promotion.getContentImg());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Promotion contentImg 삭제: PromotionId={}, URL={}", 
                            promotion.getId(), promotion.getContentImg());
                    promotion.setContentImg(null);
                    updated = true;
                }
            }

            if (updated) {
                promotionRepository.save(promotion);
                count++;
            }
        }

        return count;
    }

    /**
     * Notice 엔티티의 깨진 이미지 URL 정리
     */
    private int cleanupNoticeImages() {
        int count = 0;
        List<Notice> notices = noticeRepository.findAll();

        for (Notice notice : notices) {
            if (notice.getImageUrl() != null && !notice.getImageUrl().isEmpty()) {
                String fileId = extractFileIdFromUrl(notice.getImageUrl());
                if (fileId != null && !googleDriveService.fileExists(fileId)) {
                    log.warn("[파일 정리] Notice imageUrl 삭제: NoticeId={}, URL={}", 
                            notice.getId(), notice.getImageUrl());
                    notice.setImageUrl(null);
                    noticeRepository.save(notice);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * URL에서 Google Drive 파일 ID 추출
     */
    private String extractFileIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // https://lh3.googleusercontent.com/d/{FILE_ID} 형식
        Matcher matcher = DRIVE_IMAGE_URL_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // https://drive.google.com/uc?export=view&id={FILE_ID} 형식
        matcher = DRIVE_FILE_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * 수동으로 정리 실행 (관리자용)
     */
    @Transactional
    public int manualCleanup() {
        log.info("[파일 정리] 수동 정리 시작");
        return cleanupOrphanedFiles();
    }
}

