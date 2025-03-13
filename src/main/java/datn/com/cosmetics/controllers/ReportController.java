package datn.com.cosmetics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.response.CategoryRevenueDTO;
import datn.com.cosmetics.bean.response.MonthlyRevenueDTO;
import datn.com.cosmetics.bean.response.OrderStatusDTO;
import datn.com.cosmetics.services.IReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private IReportService reportService;

    @GetMapping("/revenue")
    public List<MonthlyRevenueDTO> getMonthlyRevenue(@RequestParam("year") int year) {
        return reportService.calculateMonthlyRevenue(year);
    }

    @GetMapping("/category-revenue")
    public List<CategoryRevenueDTO> getCategoryRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return reportService.calculateCategoryRevenue(month, year);
    }
   // get order status statistics
    @GetMapping("/order-status-counts")
    public List<OrderStatusDTO> getOrderStatusStatistics() {
        return reportService.getOrderStatusStatistics();
    }
}
