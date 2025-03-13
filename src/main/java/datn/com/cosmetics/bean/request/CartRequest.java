package datn.com.cosmetics.bean.request;

import lombok.Data;

@Data
public class CartRequest {
    private Long productId;
    private int quantity;
} 