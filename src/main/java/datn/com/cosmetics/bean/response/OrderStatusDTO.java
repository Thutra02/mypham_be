package datn.com.cosmetics.bean.response;

import datn.com.cosmetics.entity.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusDTO {
    private OrderStatus status;
    private Long orderCount;

    public OrderStatusDTO(OrderStatus status, Long orderCount) {
        this.status = status;
        this.orderCount = orderCount;
    }
}