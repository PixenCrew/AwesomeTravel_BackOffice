package renewal.awesome_travel_backoffice.image;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LocalUploader {

    @Value("${imageLocation}")
    private String uploadDir;

    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + "_" + originalFilename;

        File targetFile = new File(uploadDir, newFilename);

        // 디렉토리 없으면 생성
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // 접근 가능한 URL 리턴
        return "/images/notice/" + newFilename;
    }
}
