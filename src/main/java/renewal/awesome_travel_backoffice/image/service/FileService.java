package renewal.awesome_travel_backoffice.image.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.java.Log;

@Service
@Log
public class FileService {

    @Value("${imageLocation}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) throws Exception {
        // 경로 없는경우 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 원본 파일명 확인 및 확장자 추출
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다: " + originalFilename);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (extension.isBlank()) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다: " + originalFilename);
        }

        // 3. 파일 내용 이미지 검증 (ImageIO는 WebP 등 일부 형식 미지원 → null이어도 저장 허용)
        try (InputStream is = file.getInputStream()) {
            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                String ext = extension.toLowerCase();
                if (ext.equals(".webp") || ext.equals(".heic") || ext.equals(".avif")) {
                    log.warning("이미지 형식(" + ext + ")은 Java ImageIO에서 미지원. 파일만 저장합니다: " + originalFilename);
                } else {
                    throw new IllegalArgumentException("실제 이미지 파일이 아닙니다. JPG, PNG, GIF를 사용해 주세요: " + originalFilename);
                }
            }
        }
        
        // 4. UUID 기반 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + extension;
        String fileUploadFullUrl = uploadDir + "/" + savedFileName;
        log.info(fileUploadFullUrl);

        // 5. 상위 폴더 생성
        Path path = Paths.get(fileUploadFullUrl);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 6. 파일 저장 (try-with-resources)
        try (FileOutputStream fos = new FileOutputStream(fileUploadFullUrl)) {
            fos.write(file.getBytes());
        }
        
        return savedFileName;
    }

    public boolean deleteFile(String filePath) throws Exception {
        Path path = Paths.get(".").resolve(filePath.substring(1));
        log.info("삭제 대상 : " + path.toString());

        if (Files.exists(path)) {
            Files.delete(path);
            log.info("파일을 삭제했습니다.");
            return true;
        } else {
            log.info("파일이 존재하지 않습니다.");
            return false;
        }
    }

    // // 파일 이동 ( 원본 경로, 원본경로 기준 대상 폴더)
    // public void moveFile(String filePath, String destinationFolder) throws
    // Exception {
    // Path sourcePath = Paths.get(filePath);
    // Path destinationPath = Paths.get(destinationFolder + "/" + filePath);

    // try {
    // // 파일 이동
    // Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    // log.info("파일이 성공적으로 이동되었습니다.");
    // } catch (IOException e) {
    // System.err.println("파일 이동 중 오류 발생: " + e.getMessage());
    // }

    // }
}
