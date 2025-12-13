package renewal.awesome_travel_backoffice.image.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import renewal.awesome_travel_backoffice.image.dto.FileUploadResponse;
import renewal.awesome_travel_backoffice.image.dto.UploadedFileDetail;
import renewal.awesome_travel_backoffice.image.dto.UploadedFileListResponse;
import renewal.awesome_travel_backoffice.image.service.FileUploadService;
import renewal.awesome_travel_backoffice.image.entity.UploadedFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FileController {

    private final FileUploadService fileUploadService;

    /**
     * 파일 목록 조회 (관리자 전용)
     */
    @GetMapping("")
    public ResponseEntity<UploadedFileListResponse> listUploadedFiles() {
        List<UploadedFile> files = fileUploadService.listFiles();
        
        List<UploadedFileDetail> fileDetails = files.stream()
                .map(this::toDetail)
                .collect(Collectors.toList());

        UploadedFileListResponse response = UploadedFileListResponse.builder()
                .files(fileDetails)
                .totalCount(fileDetails.size())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 단일 파일 상세 조회
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<UploadedFileDetail> getFileDetail(@PathVariable Long fileId) {
        UploadedFile file = fileUploadService.getFile(fileId);
        
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDetail(file));
    }

    /**
     * Google Drive 뷰 링크 또는 로컬 파일 경로 반환
     */
    @GetMapping("/{fileId}/view")
    public ResponseEntity<?> getFileViewLink(@PathVariable Long fileId) {
        UploadedFile file = fileUploadService.getFile(fileId);
        
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        if (file.getDriveWebViewLink() != null) {
            return ResponseEntity.ok(new FileViewResponse(fileId, file.getDriveWebViewLink(), null));
        } else if (file.getFilePath() != null) {
            return ResponseEntity.ok(new FileViewResponse(fileId, null, file.getFilePath()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("파일 경로를 찾을 수 없습니다.");
        }
    }

    /**
     * 파일 다운로드 (Drive or Local)
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
        UploadedFile file = fileUploadService.getFile(fileId);
        
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        // Google Drive 다운로드 링크로 리디렉션
        if (file.getDriveDownloadLink() != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", file.getDriveDownloadLink())
                    .build();
        }
        // 로컬 파일 다운로드 (실제 구현은 별도 필요)
        else if (file.getFilePath() != null) {
            // TODO: 로컬 파일 다운로드 구현
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                    .body("로컬 파일 다운로드는 아직 구현되지 않았습니다.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("다운로드 가능한 파일이 없습니다.");
    }

    /**
     * 파일 업로드 (관리자 전용)
     * @param file 업로드할 파일
     * @param folderType 폴더 타입 (product, notice, banner, popup, promotion, hotel, excel 등)
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderType", required = false) String folderType) {
        try {
            UploadedFile uploadedFile = fileUploadService.uploadFile(file, folderType);

            FileUploadResponse response = FileUploadResponse.builder()
                    .message("파일 업로드 완료")
                    .fileId(uploadedFile.getId())
                    .filename(uploadedFile.getOriginalFilename())
                    .driveLink(uploadedFile.getDriveWebViewLink())
                    .imageUrl(uploadedFile.getDriveImageUrl())  // 이미지 직접 표시용 URL
                    .uploadType(uploadedFile.getUploadType())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(FileUploadResponse.builder()
                            .message("파일 업로드 실패: " + e.getMessage())
                            .build());
        }
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        try {
            boolean deleted = fileUploadService.deleteFile(fileId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * UploadedFile을 UploadedFileDetail로 변환
     */
    private UploadedFileDetail toDetail(UploadedFile file) {
        return UploadedFileDetail.builder()
                .id(file.getId())
                .originalFilename(file.getOriginalFilename())
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .uploadType(file.getUploadType())
                .driveFileId(file.getDriveFileId())
                .driveWebViewLink(file.getDriveWebViewLink())
                .driveImageUrl(file.getDriveImageUrl())  // 이미지 직접 표시용 URL
                .driveDownloadLink(file.getDriveDownloadLink())
                .filePath(file.getFilePath())
                .createdAt(file.getCreatedAt())
                .build();
    }

    /**
     * 파일 뷰 응답 DTO
     */
    private static class FileViewResponse {
        private Long fileId;
        private String viewLink;
        private String localPath;

        public FileViewResponse(Long fileId, String viewLink, String localPath) {
            this.fileId = fileId;
            this.viewLink = viewLink;
            this.localPath = localPath;
        }

        public Long getFileId() { return fileId; }
        public String getViewLink() { return viewLink; }
        public String getLocalPath() { return localPath; }
    }

    /**
     * 깨진 이미지 URL 정리 (수동 실행)
     * Google Drive에서 수동으로 삭제된 파일의 DB 레코드를 정리합니다.
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOrphanedFiles() {
        try {
            int cleanedCount = fileUploadService.cleanupOrphanedFiles();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "파일 정리가 완료되었습니다.");
            response.put("cleanedCount", cleanedCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "파일 정리 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

