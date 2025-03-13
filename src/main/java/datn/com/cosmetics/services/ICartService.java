package datn.com.cosmetics.services;

import java.math.BigDecimal;

import datn.com.cosmetics.bean.request.CartRequest;
import datn.com.cosmetics.entity.Cart;

public interface ICartService {
    Cart addToCart(CartRequest cartRequest,String username);
    Cart updateQuantity(Long cartItemId, int quantity);
    void clearCart(String user);
    void removeCartItem(Long cartItemId);
    Cart getCartUser(String username); // New method
    BigDecimal calculateTotalAmount(Cart cart); // New method
}
