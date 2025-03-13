package datn.com.cosmetics.services;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.OrderRequest;
import datn.com.cosmetics.entity.Order;

public interface IOrderService { // Fixed typo in interface name
    Order createOrder(OrderRequest order);

    Order getOrderById(Long id);

    Page<Order> getAllOrders(Pageable pageable);

    List<Order> getOrdersByUser(String username);

    Order changeStatusOrder(Long id, String status); // Fixed typo in method name

    Order updateOrderStatusMomo( String id, String status);
    void deleteOrder(Long id);
    Order findByOrderId(String orderId);
    public byte[] generateOrderPdf(Order order) throws IOException;
    Page<Order> searchOrdersByOrderId(String orderId, Pageable pageable);
}
