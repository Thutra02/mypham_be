package datn.com.cosmetics.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.MomoPaymentRequest;
import datn.com.cosmetics.bean.response.MomoResponse;
import datn.com.cosmetics.config.MoMoConfig;
import datn.com.cosmetics.entity.Order;
import datn.com.cosmetics.services.ICartService;
import datn.com.cosmetics.services.IOrderService;
import datn.com.cosmetics.services.MoMoPaymentService;

@RestController
@RequestMapping("/api/momo")
public class MomoController {
    @Autowired
    private MoMoConfig moMoConfig;

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    @PostMapping("")
    public ResponseEntity<?> paymentWithMomo(@RequestBody MomoPaymentRequest request) {
        try {
            String payUrl = moMoPaymentService.paymentWithMomo(request.getOrderId(), request.getTotal());
            return ResponseEntity.ok(payUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(
                    Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/callback")
    public String callback(@RequestBody MomoResponse response) {
        
        System.out.println(response);
        Order order = orderService.findByOrderId(response.getOrderId());
        if (order == null) {
            return "";
        }

        if (response.getResultCode() == 0) {
            // convert id string to long
            orderService.updateOrderStatusMomo(response.getOrderId(), "PROCESSING");
            cartService.clearCart(order.getUser().getEmail());

        } else {
            // xoá order và order item 
            orderService.deleteOrder(order.getId());
        }
        return "";
    }

    @GetMapping("/ipn")
    public String ipn(@RequestParam String orderId, @RequestParam String amount) {
        return "redirect:" + moMoConfig.getRedirectUrl() + "?orderId=" + orderId + "&amount=" + amount;
    }

}
