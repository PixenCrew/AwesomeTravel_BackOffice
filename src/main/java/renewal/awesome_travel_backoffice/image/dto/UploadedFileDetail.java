package renewal.awesome_travel_backoffice.image.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileDetail {
    private Long id;
    private String originalFilename;
    private String mimeType;
    private Long fileSize;
    private String uploadType;
    private String driveFileId;
    private String driveWebViewLink;
    private String driveImageUrl;  // 이미지 직접 표시용 URL (<img> 태그에서 사용) ⭐
    private String driveDownloadLink;
    private String filePath;
    private LocalDateTime createdAt;
}


