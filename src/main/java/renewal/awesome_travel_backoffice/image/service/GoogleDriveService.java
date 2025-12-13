package renewal.awesome_travel_backoffice.image.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "AwesomeTravel BackOffice";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    @Value("${google.drive.credentials.path:}")
    private String credentialsPath;

    @Value("${google.drive.folder.id:}")
    private String defaultFolderId; // 기본 업로드할 Google Drive 폴더 ID

    @Value("${google.client.id:}")
    private String clientId;

    @Value("${google.client.secret:}")
    private String clientSecret;

    @Value("${google.drive.refresh.token:}")
    private String refreshToken; // OAuth 2.0 Refresh Token (개인 계정 사용 시)

    /**
     * Google Drive API 인증 및 Drive 서비스 생성
     * 개인 Google 계정(OAuth 2.0) 또는 서비스 계정 지원
     */
    private Drive getDriveService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        
        GoogleCredentials credentials;
        
        // OAuth 2.0 Refresh Token이 있으면 개인 계정 사용
        if (refreshToken != null && !refreshToken.isEmpty() && 
            clientId != null && !clientId.isEmpty() && 
            clientSecret != null && !clientSecret.isEmpty()) {
            
            log.info("OAuth 2.0 방식으로 개인 Google 계정 사용");
            
            // UserCredentials로 Refresh Token 사용
            credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();
            
            // Access Token 갱신
            credentials.refreshIfExpired();
            
        } else if (credentialsPath != null && !credentialsPath.isEmpty()) {
            // 서비스 계정 키 파일 경로가 설정된 경우
            log.info("서비스 계정 방식 사용");
            
            java.io.InputStream credentialsStream = getClass().getClassLoader()
                    .getResourceAsStream(credentialsPath);
            
            if (credentialsStream == null) {
                // 클래스패스에서 찾지 못하면 파일 시스템에서 시도
                java.io.File credentialsFile = new java.io.File(credentialsPath);
                if (credentialsFile.exists()) {
                    credentialsStream = new java.io.FileInputStream(credentialsFile);
                } else {
                    throw new IOException("Google Drive 인증 파일을 찾을 수 없습니다: " + credentialsPath);
                }
            }
            
            try {
                // 서비스 계정인지 OAuth 클라이언트인지 확인
                String content = new String(credentialsStream.readAllBytes());
                credentialsStream.close();
                
                if (content.contains("\"type\":\"service_account\"")) {
                    // 서비스 계정
                    credentials = com.google.auth.oauth2.ServiceAccountCredentials
                            .fromStream(new java.io.ByteArrayInputStream(content.getBytes()))
                            .createScoped(SCOPES);
                } else {
                    throw new IllegalStateException(
                        "서비스 계정 키 파일이 필요합니다. OAuth 클라이언트 ID 파일이 아닙니다. " +
                        "개인 계정을 사용하려면 google.drive.refresh.token을 설정하세요.");
                }
            } finally {
                if (credentialsStream != null) {
                    credentialsStream.close();
                }
            }
        } else {
            throw new IllegalStateException(
                "Google Drive 인증 설정이 없습니다. " +
                "개인 계정: google.drive.refresh.token 설정 필요. " +
                "서비스 계정: google.drive.credentials.path 설정 필요.");
        }

        return new Drive.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * 파일을 Google Drive에 업로드
     * @param multipartFile 업로드할 파일
     * @param folderId 업로드할 폴더 ID (null이면 기본 폴더 사용)
     */
    public DriveUploadResult uploadFile(MultipartFile multipartFile, String folderId) throws Exception {
        Drive driveService = getDriveService();

        // 파일 메타데이터 생성
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        
        // 폴더 ID 결정: 파라미터 > 기본값
        String targetFolderId = (folderId != null && !folderId.isEmpty()) ? folderId : defaultFolderId;
        
        // 폴더 ID가 설정되어 있으면 해당 폴더에 업로드
        if (targetFolderId != null && !targetFolderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(targetFolderId));
        }
        
        // 파일 내용 설정
        byte[] fileBytes = multipartFile.getBytes();
        String mimeType = multipartFile.getContentType();
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        
        ByteArrayContent mediaContent = new ByteArrayContent(mimeType, fileBytes);

        // 파일 업로드 (서비스 계정은 공유된 폴더에만 업로드 가능)
        com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, name, webViewLink, webContentLink, mimeType, size")
                .setSupportsAllDrives(true)  // Shared Drive 지원
                .execute();

        log.info("Google Drive 업로드 완료: File ID = {}", uploadedFile.getId());

        // 결과 반환
        DriveUploadResult result = new DriveUploadResult();
        result.setFileId(uploadedFile.getId());
        result.setFileName(uploadedFile.getName());
        result.setWebViewLink(uploadedFile.getWebViewLink());
        result.setDownloadLink(uploadedFile.getWebContentLink());
        result.setMimeType(uploadedFile.getMimeType());
        result.setFileSize(uploadedFile.getSize() != null ? uploadedFile.getSize() : fileBytes.length);
        
        // 이미지 직접 표시용 URL 생성 (클라이언트에서 <img> 태그로 사용)
        // Google 이미지 CDN 사용 (가장 안정적) ⭐
        // 참고: 파일이 공개 설정되어 있어야 함
        String imageUrl = "https://lh3.googleusercontent.com/d/" + uploadedFile.getId();
        result.setImageUrl(imageUrl);

        return result;
    }

    /**
     * Google Drive에서 파일 삭제
     */
    public void deleteFile(String fileId) throws Exception {
        Drive driveService = getDriveService();
        driveService.files().delete(fileId)
                .setSupportsAllDrives(true)  // Shared Drive 지원
                .execute();
        log.info("Google Drive 파일 삭제 완료: File ID = {}", fileId);
    }

    /**
     * Google Drive에서 파일 존재 여부 확인
     */
    public boolean fileExists(String fileId) {
        try {
            Drive driveService = getDriveService();
            driveService.files().get(fileId)
                    .setFields("id")
                    .setSupportsAllDrives(true)
                    .execute();
            return true;
        } catch (Exception e) {
            // 404 Not Found 또는 기타 오류는 파일이 존재하지 않음을 의미
            log.debug("Google Drive 파일 확인 실패: File ID = {}, Error = {}", fileId, e.getMessage());
            return false;
        }
    }

    /**
     * Google Drive 업로드 결과 DTO
     */
    public static class DriveUploadResult {
        private String fileId;
        private String fileName;
        private String webViewLink;      // Google Drive 뷰어 페이지 링크
        private String imageUrl;         // 이미지 직접 표시용 URL (<img> 태그에서 사용) ⭐
        private String downloadLink;
        private String mimeType;
        private Long fileSize;

        // Getters and Setters
        public String getFileId() { return fileId; }
        public void setFileId(String fileId) { this.fileId = fileId; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public String getWebViewLink() { return webViewLink; }
        public void setWebViewLink(String webViewLink) { this.webViewLink = webViewLink; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getDownloadLink() { return downloadLink; }
        public void setDownloadLink(String downloadLink) { this.downloadLink = downloadLink; }

        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }

        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    }
}

