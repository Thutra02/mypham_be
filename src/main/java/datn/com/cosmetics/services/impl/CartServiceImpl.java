package datn.com.cosmetics.services.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.request.CartRequest;
import datn.com.cosmetics.entity.Cart;
import datn.com.cosmetics.entity.CartItem;
import datn.com.cosmetics.entity.Product;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.repository.CartItemRepository;
import datn.com.cosmetics.repository.CartRepository;
import datn.com.cosmetics.repository.ProductRepository;
import datn.com.cosmetics.repository.UserRepository;
import datn.com.cosmetics.services.ICartService;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Cart addToCart(CartRequest cartRequest, String username) {
        User user = userRepository.findByEmail(username);
        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock()<cartRequest.getQuantity()) {
            throw new RuntimeException("Product quantity is not enough");
        }       

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotal(0);
            return cartRepository.save(newCart);
        });

        BigDecimal unitPrice =  product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartRequest.getQuantity());
            cartItem.setUnitPrice(unitPrice);
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        cart.setTotal(cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .mapToDouble(BigDecimal::doubleValue).sum());
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        cart.setTotal(cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .mapToDouble(BigDecimal::doubleValue).sum());
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(String username) {
        User user = userRepository.findByEmail(username);
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotal(0);
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartUser(String username) {
        User user = userRepository.findByEmail(username);
        return cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    public BigDecimal calculateTotalAmount(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        cart.setTotal(cart.getCartItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .mapToDouble(BigDecimal::doubleValue).sum());
        cartRepository.save(cart);
    }

}
