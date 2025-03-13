package datn.com.cosmetics.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryRevenueDTO {
    private String categoryName;
    private double totalRevenue;
    private double totalDiscountedRevenue;
    private long totalOrders;
}