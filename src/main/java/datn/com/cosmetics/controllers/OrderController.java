package datn.com.cosmetics.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.OrderRequest;
import datn.com.cosmetics.bean.request.StatusRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Order;
import datn.com.cosmetics.services.EmailService;
import datn.com.cosmetics.services.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "The Order API")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private EmailService emailService;

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order with the provided details")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Parameter(description = "Order request body", required = true) @RequestBody OrderRequest orderRequest,
            @Parameter(description = "Authorization token", required = true) @RequestHeader("Authorization") String token) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            }
            String username = authentication.getName();
            orderRequest.setUsername(username);
            Order createdOrder = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(ApiResponse.success(createdOrder, "Order created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve an order by its unique ID")
    public ResponseEntity<ApiResponse<Order>> getOrderById(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error"));
        }
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders with pagination or search by order ID")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(Pageable pageable,
            @Parameter(description = "Order ID to search for", required = false) @RequestParam(required = false) String orderId) {
        try {
            Page<Order> orders;
            if (orderId != null && !orderId.isEmpty()) {
                orders = orderService.searchOrdersByOrderId(orderId, pageable);
            } else {
                orders = orderService.getAllOrders(pageable);
            }
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(orders.getNumber() + 1, orders.getTotalPages(),
                    orders.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(orders.getContent(), "Orders retrieved successfully", pagination));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error"));
        }
    }

    @GetMapping("/user")
    @Operation(summary = "Get orders by user", description = "Retrieve all orders for the authenticated user")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUser(
            @Parameter(description = "Authorization token", required = true) @RequestHeader("Authorization") String token) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
            }
            String username = authentication.getName();
            List<Order> orders = orderService.getOrdersByUser(username);
            return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error"));
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Change order status", description = "Change the status of an order by its ID")
    public ResponseEntity<ApiResponse<Order>> changeStatusOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @Parameter(description = "New status", required = true) @RequestBody StatusRequest status) {
        try {
            Order updatedOrder = orderService.changeStatusOrder(id, status.getStatus());
            return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Order status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Delete an order by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Order deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error"));
        }
    }

    @GetMapping("/{orderId}/download-pdf")
    public ResponseEntity<byte[]> downloadOrderPdf(@PathVariable Long orderId) throws IOException {
        try {
            Order order = orderService.getOrderById(orderId);
            byte[] pdfBytes = orderService.generateOrderPdf(order);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_" + orderId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/send-email/{orderId}")
    public ResponseEntity<ApiResponse<Void>> sendOrderEmail(@PathVariable Long orderId, @RequestBody String email) {
        try {
            Order order = orderService.getOrderById(orderId);
            Map<String, Object> model = new HashMap<>();
            model.put("order", order);
            emailService.sendOrderEmail(email, "Chi tiết đơn hàng #" + order.getOrderId(), "order-email", model);
            return ResponseEntity.ok(ApiResponse.success(null, "Email sent successfully"));
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to send email"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error"));
        }
    }
}
