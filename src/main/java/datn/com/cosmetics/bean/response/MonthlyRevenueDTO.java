package datn.com.cosmetics.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private int month;
    private int year;
    private double totalRevenue;
    private double totalDiscountedRevenue;
    private long totalOrders;
}
