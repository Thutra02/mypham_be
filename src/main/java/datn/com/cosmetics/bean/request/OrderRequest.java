
package datn.com.cosmetics.bean.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    String username;
    Long address;
    String paymentMethod;
    List<OrderItemRequest> orderItems;
    BigDecimal totalAmount;
    BigDecimal discountAmount;
    BigDecimal finalAmount;
    String discountCode;

}