package renewal.awesome_travel_backoffice.image.dto;

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
public class FileUploadResponse {
    private String message;
    private Long fileId;
    private String filename;
    private String driveLink; // Google Drive web view link
    private String imageUrl;  // 이미지 직접 표시용 URL (<img> 태그에서 사용) ⭐
    private String uploadType; // "DRIVE" or "LOCAL"
}


