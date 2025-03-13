package datn.com.cosmetics.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import datn.com.cosmetics.bean.request.OrderRequest;
import datn.com.cosmetics.entity.Address;
import datn.com.cosmetics.entity.Cart;
import datn.com.cosmetics.entity.Discount;
import datn.com.cosmetics.entity.Order;
import datn.com.cosmetics.entity.OrderItem;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.entity.enums.OrderStatus;
import datn.com.cosmetics.repository.AddressRepository;
import datn.com.cosmetics.repository.CartItemRepository;
import datn.com.cosmetics.repository.CartRepository;
import datn.com.cosmetics.repository.DiscountRepository;
import datn.com.cosmetics.repository.OrderItemRepository;
import datn.com.cosmetics.repository.OrderRepository;
import datn.com.cosmetics.repository.ProductRepository;
import datn.com.cosmetics.repository.UserRepository;
import datn.com.cosmetics.services.IOrderService;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        try {
            System.out.println(
                    "orderRequest: " + orderRequest.getUsername() + " " + orderRequest.getAddress() + " "
                            + orderRequest.getPaymentMethod() + " " + orderRequest.getTotalAmount() + " "
                            + orderRequest.getDiscountAmount() + " " + orderRequest.getOrderItems());

            // Validate input
            if (orderRequest.getTotalAmount() == null || orderRequest.getOrderItems().isEmpty()) {
                throw new IllegalArgumentException("Invalid order request");
            }

            User user = userRepository.findByEmail(orderRequest.getUsername());
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            Cart cart = cartRepository.findByUser(user).orElse(null);

            // Kiểm tra address
            Address address = addressRepository.findById(orderRequest.getAddress())
                    .orElseThrow(() -> new IllegalArgumentException("Address not found"));

            if (!address.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Address does not belong to user");
            }

            // Kiểm tra phương thức thanh toán
            if (!isValidPaymentMethod(orderRequest.getPaymentMethod())) {
                throw new IllegalArgumentException("Invalid payment method: " + orderRequest.getPaymentMethod());
            }
            Order order = new Order();
            System.out.println("cart: " + orderRequest.getDiscountCode().isEmpty());

            // kiểm tra mã giảm giá trong giỏ hàng có trùng với mã giảm giá trong order
            if (orderRequest.getDiscountCode().isEmpty() == false) {
                if (!orderRequest.getDiscountCode().equals(cart.getDiscountCode())) {
                    throw new IllegalArgumentException("Invalid discount code: " + orderRequest.getDiscountCode());
                }
                Discount discount = discountRepository.findByDiscountCode(orderRequest.getDiscountCode());
                if (discount == null || !discount.isActive()) {
                    throw new IllegalArgumentException("Discount code is not valid or inactive");
                }
                if (discount.getUsageCount() >= discount.getMaxUsage()) {
                    throw new IllegalArgumentException("Discount code usage limit reached");
                }
                order.setDiscount(discount);
                // Decrement the discount usage count
                discount.setUsageCount(discount.getUsageCount() + 1);
                discountRepository.save(discount);
            }

            // **Fix lỗi Duplicate Entry**
            // Tạo order mới với ID tự động sinh
            order.setUser(user);
            order.setShippingAddress(address);
            order.setTotalAmount(orderRequest.getTotalAmount());
            order.setDiscountAmount(orderRequest.getDiscountAmount());
            order.setFinalAmount(orderRequest.getFinalAmount());
            order.setStatus(OrderStatus.PENDING);
            order.setPaymentMethod(orderRequest.getPaymentMethod());

            // **Lưu order trước khi lưu OrderItems**
            Order newOrder = orderRepository.save(order);

            // **Lưu OrderItem**
            orderRequest.getOrderItems().forEach(itemRequest -> {
                if (itemRequest.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive");
                }
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(newOrder);
                orderItem.setProduct(productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found")));
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setUnitPrice(itemRequest.getPrice());

                orderItemRepository.save(orderItem);
            });

            // nếu là cod thì xóa cart
            if (orderRequest.getPaymentMethod().equals("COD")) {

                if (cart != null) {
                    cartItemRepository.deleteByCartId(cart.getId());
                    cart.setTotal(0);
                    cart.setDiscountCode(null);
                    cartRepository.save(cart);
                }
            }

            return newOrder;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order" +e.getMessage(), e);
        }
    }

    @Override
    public Order getOrderById(Long id) {
        try {
            return orderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve order", e);
        }
    }

    @Override
    public Order findByOrderId(String orderId) {
        try {
            return orderRepository.findByIdOrder(orderId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve order", e);
        }
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        try {
            return orderRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders", e);
        }
    }

    @Override
    @Transactional
    public Order changeStatusOrder(Long id, String status) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            if (!isValidStatus(status)) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new IllegalArgumentException("Order has been cancelled");
            }
            order.setStatus(OrderStatus.valueOf(status));
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to change order status", e);
        }
    }

    @Override
    @Transactional
    public Order updateOrderStatusMomo(String id, String status) {
        try {
            Order order = orderRepository.findByIdOrder(id);
            if (order == null) {
                throw new IllegalArgumentException("Order not found");
            }
            if (!isValidStatus(status)) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new IllegalArgumentException("Order has been cancelled");
            }
            order.setStatus(OrderStatus.valueOf(status));
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            orderItemRepository.deleteByOrder(order); // Xóa OrderItem trước
            orderRepository.delete(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete order", e);
        }
    }

    @Override
    public List<Order> getOrdersByUser(String username) {
        try {
            User user = userRepository.findByEmail(username);
            if (user == null) {
                return Collections.emptyList();
            }
            return orderRepository.findByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders by user", e);
        }
    }

    @Override
    public Page<Order> searchOrdersByOrderId(String orderId, Pageable pageable) {
        try {
            return orderRepository.searchByOrderId(orderId, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search orders by order ID", e);
        }
    }

    private boolean isValidStatus(String status) {
        return OrderStatus.valueOf(status) != null;
    }

    private boolean isValidPaymentMethod(String paymentMethod) {
        return List.of("COD", "CREDIT_CARD", "MOMO").contains(paymentMethod);
    }

    @Override
    public byte[] generateOrderPdf(Order order) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Thiết lập font trước khi hiển thị text
                PDFont font = PDType1Font.HELVETICA; // Thay thế Helvetica

                float fontSize = 12;
                contentStream.setFont(font, fontSize);

                // Tiêu đề hóa đơn
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hóa đơn đơn hàng #" + order.getOrderId());
                contentStream.endText();

                // Ngày đặt hàng
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 670);
                contentStream.showText("Ngày đặt hàng: " + order.getOrderDate());
                contentStream.endText();

                // Bảng sản phẩm
                float startY = 640;
                contentStream.beginText();
                contentStream.newLineAtOffset(100, startY);
                contentStream.showText("Sản phẩm       Số lượng     Đơn giá     Thành tiền");
                contentStream.endText();

                List<OrderItem> orderItems = order.getOrderItems();
                for (OrderItem item : orderItems) {
                    startY -= 20;
                    BigDecimal total = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, startY);
                    contentStream.showText(String.format("%-15s %-10d %-10s %-10s",
                            item.getProduct().getName(), item.getQuantity(),
                            item.getUnitPrice(), total));
                    contentStream.endText();
                }

                // Tổng tiền
                startY -= 40;
                contentStream.beginText();
                contentStream.newLineAtOffset(100, startY);
                contentStream.showText("Tổng tiền: " + order.getFinalAmount() + " VND");
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IOException("Failed to generate PDF", e);
        }
    }

}