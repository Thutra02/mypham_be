package datn.com.cosmetics.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServiceImpl implements IUploadService {

    @Autowired
    private FileStorageService fileStorageService;
    private final Path uploadDir = Paths.get("uploads");

    @Override
    public List<String> uploadImages(MultipartFile[] files) throws IOException {
        return fileStorageService.storeMultipleFiles(files).stream()
                .map(fileName -> "/uploads/" + fileName)
                .collect(Collectors.toList());
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return "/uploads/" + fileStorageService.storeFile(file);
    }

    @Override
    public List<String> getUploadedImages() throws IOException {
        return fileStorageService.loadAllFiles().stream()
                .map(fileName -> "/uploads/" + fileName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFile(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize(); // Chuẩn hóa đường dẫn để tránh lỗi
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                 return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
