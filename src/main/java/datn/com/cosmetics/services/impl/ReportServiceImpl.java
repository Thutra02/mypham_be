package datn.com.cosmetics.services.impl;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.response.CategoryRevenueDTO;
import datn.com.cosmetics.bean.response.MonthlyRevenueDTO;
import datn.com.cosmetics.bean.response.OrderStatusDTO;
import datn.com.cosmetics.entity.enums.OrderStatus;
import datn.com.cosmetics.repository.OrderItemRepository;
import datn.com.cosmetics.repository.OrderRepository;
import datn.com.cosmetics.services.IReportService;

@Service
public class ReportServiceImpl implements IReportService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<MonthlyRevenueDTO> calculateMonthlyRevenue(Integer year) {
        List<Object[]> results;
        if (year != null) {
            results = orderRepository.getMonthlyRevenueByYear(year);
        } else {
            results = orderRepository.getMonthlyRevenue();
        }

        // Tạo map để lưu dữ liệu doanh thu theo tháng
        Map<YearMonth, MonthlyRevenueDTO> revenueMap = new HashMap<>();

        // Nếu có năm cụ thể, tạo 12 tháng mặc định với doanh thu = 0
        if (year != null) {
            for (int i = 1; i <= 12; i++) {
                YearMonth ym = YearMonth.of(year, i);
                revenueMap.put(ym, new MonthlyRevenueDTO(i, year, 0.0, 0.0, 0));
            }
        }

        // Điền dữ liệu từ database vào map
        for (Object[] obj : results) {
            int month = (int) obj[0];
            int resultYear = (int) obj[1];
            double totalRevenue = (double) obj[2];
            double totalDiscountedRevenue = (double) obj[3];
            long totalOrders = (long) obj[4];

            YearMonth ym = YearMonth.of(resultYear, month);
            revenueMap.put(ym,
                    new MonthlyRevenueDTO(month, resultYear, totalRevenue, totalDiscountedRevenue, totalOrders));
        }

        // Trả về danh sách đã sắp xếp theo tháng và năm
        return revenueMap.values().stream()
                .sorted(Comparator.comparing(MonthlyRevenueDTO::getYear).reversed()
                        .thenComparing(MonthlyRevenueDTO::getMonth).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryRevenueDTO> calculateCategoryRevenue(Integer month, Integer year) {
        List<Object[]> results;

        if (month != null && year != null) {
            results = orderItemRepository.getCategoryRevenueByMonthAndYear(OrderStatus.DELIVERED, month, year);
        } else if (year != null) {
            results = orderItemRepository.getCategoryRevenueByYear(OrderStatus.DELIVERED, year);
        } else {
            results = orderItemRepository.getCategoryRevenue(OrderStatus.DELIVERED);
        }

        return results.stream().map(obj -> new CategoryRevenueDTO(
                (String) obj[0], // categoryName
                (double) obj[1], // totalRevenue
                (double) obj[2], // totalDiscountedRevenue
                (long) obj[3] // totalOrders
        )).collect(Collectors.toList());

    }

    @Override
    public List<OrderStatusDTO> getOrderStatusStatistics() {
        List<Object[]> results = orderRepository.getOrderStatusStatistics();
        return results.stream().map(obj -> new OrderStatusDTO(
                (OrderStatus) obj[0], // status
                (Long) obj[1] // orderCount
        )).collect(Collectors.toList());
    }

}
