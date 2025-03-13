package datn.com.cosmetics.bean.request;

import lombok.Data;

@Data
public class ReviewRequest {
    private String review;
    private float star;
    private Long productId;
    private Long orderId;
 
}
