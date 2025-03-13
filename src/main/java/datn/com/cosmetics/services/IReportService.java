package datn.com.cosmetics.services;

import java.util.List;

import datn.com.cosmetics.bean.response.CategoryRevenueDTO;
import datn.com.cosmetics.bean.response.MonthlyRevenueDTO;
import datn.com.cosmetics.bean.response.OrderStatusDTO;

public interface IReportService {
    List<MonthlyRevenueDTO> calculateMonthlyRevenue(Integer year);

    List<CategoryRevenueDTO> calculateCategoryRevenue(Integer month, Integer year);

    List<OrderStatusDTO> getOrderStatusStatistics();
}
