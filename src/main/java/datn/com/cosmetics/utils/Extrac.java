package datn.com.cosmetics.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Extrac {

    public String extractFilenameFromUrl(String fileUrl) {
        try {
            // Giải mã URL (phòng trường hợp chứa ký tự đặc biệt)
            String decodedUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);

            // Lấy phần cuối của URL (sau dấu "/")
            return decodedUrl.substring(decodedUrl.lastIndexOf("/") + 1);
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL format");
        }
    }
}