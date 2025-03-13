package datn.com.cosmetics.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            // Tạo thư mục nếu chưa có
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Định danh file với UUID để tránh trùng tên
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Lưu file
            Files.copy(file.getInputStream(), filePath);

            return fileName; // Trả về tên file để sử dụng sau này
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file", e);
        }
    }

    public List<String> storeMultipleFiles(MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            fileNames.add(storeFile(file));
        }
        return fileNames;
    }

    public boolean deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa file: " + fileName, e);
        }
    }

    public List<String> deleteMultipleFiles(List<String> fileNames) {
        List<String> deletedFiles = new ArrayList<>();
        for (String fileName : fileNames) {
            if (deleteFile(fileName)) {
                deletedFiles.add(fileName);
            }
        }
        return deletedFiles;
    }

    public List<String> loadAllFiles() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return new ArrayList<>();
            }
            return Files.list(uploadPath)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải danh sách file", e);
        }
    }

}
