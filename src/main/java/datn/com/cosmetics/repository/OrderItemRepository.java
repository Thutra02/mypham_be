package datn.com.cosmetics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Order;
import datn.com.cosmetics.entity.OrderItem;
import datn.com.cosmetics.entity.enums.OrderStatus;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
        void deleteByOrder(Order order);

        @Query("SELECT c.name AS categoryName, " +
                        "CAST(SUM(CASE WHEN p.isSale = true THEN oi.quantity * p.salePrice ELSE oi.quantity * oi.unitPrice END) AS double) AS totalRevenue, "
                        +
                        "CAST(SUM(CASE WHEN p.isSale = true THEN oi.quantity * p.salePrice ELSE oi.quantity * oi.unitPrice END) AS double) AS totalDiscountedRevenue, "
                        +
                        "COUNT(DISTINCT oi.order.id) AS totalOrders " +
                        "FROM OrderItem oi " +
                        "JOIN oi.product p " +
                        "JOIN p.category c " +
                        "WHERE oi.order.status = :status " +
                        "GROUP BY c.name " +
                        "ORDER BY totalRevenue DESC")
        List<Object[]> getCategoryRevenue(OrderStatus status);

        @Query("SELECT c.name AS categoryName, " +
                        "CAST(SUM(CASE WHEN p.isSale = true THEN oi.quantity * p.salePrice ELSE oi.quantity * oi.unitPrice END) AS double) AS totalRevenue, "
                        +
                        "CAST(SUM(CASE WHEN p.isSale = true THEN oi.quantity * p.salePrice ELSE oi.quantity * oi.unitPrice END) AS double) AS totalDiscountedRevenue, "
                        +
                        "COUNT(DISTINCT oi.order.id) AS totalOrders " +
                        "FROM OrderItem oi " +
                        "JOIN oi.product p " +
                        "JOIN p.category c " +
                        "WHERE oi.order.status = :status " +
                        "AND YEAR(oi.order.orderDate) = :year " +
                        "GROUP BY c.name " +
                        "ORDER BY totalRevenue DESC")
        List<Object[]> getCategoryRevenueByYear(OrderStatus status, int year);

        @Query("SELECT c.name AS categoryName, " +
                        "CAST(SUM(CASE WHEN p.isSale = true THEN oi.quantity * p.salePrice ELSE oi.quantity * oi.unitPrice END) AS double) AS totalRevenue, "
                        +
                        "CAST(SUM(CASE WHEN p.isSale = true THEN oi.quantity * p.salePrice ELSE oi.quantity * oi.unitPrice END) AS double) AS totalDiscountedRevenue, "
                        +
                        "COUNT(DISTINCT oi.order.id) AS totalOrders " +
                        "FROM OrderItem oi " +
                        "JOIN oi.product p " +
                        "JOIN p.category c " +
                        "WHERE oi.order.status = :status " +
                        "AND MONTH(oi.order.orderDate) = :month " +
                        "AND YEAR(oi.order.orderDate) = :year " +
                        "GROUP BY c.name " +
                        "ORDER BY totalRevenue DESC")
        List<Object[]> getCategoryRevenueByMonthAndYear(OrderStatus status, int month, int year);

        @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.user.id = :userId AND oi.product.id = :productId")
        int checkProductIsBought(Long userId, Long productId);


        // select order item  by order id and product id
        @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.product.id = :productId")
        OrderItem getOrderItemById(Long orderId, Long productId);

        
}
