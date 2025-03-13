package datn.com.cosmetics.services;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import datn.com.cosmetics.config.MoMoConfig;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoMoPaymentService {

    private final MoMoConfig moMoConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String paymentWithMomo(String orderId, double total) {
        String requestId = moMoConfig.getPartnerCode() + System.currentTimeMillis();
        String orderInfo = "pay with MoMo";
        String amount = String.valueOf((long) total); // Chuyển total thành số nguyên
        String extraData = "";
        String requestType = "payWithMethod";

        // Tạo raw signature
        String rawSignature = "accessKey=" + moMoConfig.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + moMoConfig.getIpnUrl() +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + moMoConfig.getPartnerCode() +
                "&redirectUrl=" + moMoConfig.getRedirectUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        // Tạo chữ ký HMAC SHA256
        String signature = hmacSha256(rawSignature, moMoConfig.getSecretKey());

        // Tạo request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", moMoConfig.getPartnerCode());
        requestBody.put("accessKey", moMoConfig.getAccessKey());
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", moMoConfig.getRedirectUrl());
        requestBody.put("ipnUrl", moMoConfig.getIpnUrl());
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "en");

        // Cấu hình headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gửi yêu cầu đến MoMo
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(moMoConfig.getEndpoint(), entity, String.class);

        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
            if ((int) responseMap.get("resultCode") != 0) {
                throw new RuntimeException("MoMo payment failed: " + responseMap.get("message"));
            }
            return (String) responseMap.get("payUrl");
        } catch (Exception e) {
            throw new RuntimeException("Error processing MoMo response: " + e.getMessage());
        }
    }

    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}