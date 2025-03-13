package datn.com.cosmetics.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import datn.com.cosmetics.bean.request.CartRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Cart;
import datn.com.cosmetics.entity.Product;
import datn.com.cosmetics.services.ICartService;
import datn.com.cosmetics.services.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "The Cart API")
public class CartController {

    @Autowired
    private ICartService cartService;

    @Autowired
    private IProductService productService;

    @PostMapping("/add")
    @Operation(summary = "Add item to cart", description = "Add an item to the cart with the provided details")
    public ResponseEntity<ApiResponse<Cart>> addToCart(
            @Parameter(description = "Cart request body", required = true) @RequestBody CartRequest cartRequest,
            @Parameter(description = "Authorization token", required = false) @RequestHeader("Authorization") String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }
        Product product = productService.getProductById(cartRequest.getProductId());
        if (product == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Product not found"));
        }

        String username = authentication.getName();
        Cart cart = cartService.addToCart(cartRequest, username);
        return new ResponseEntity<>(ApiResponse.success(cart, "Item added to cart successfully"), HttpStatus.OK);
    }

    @PutMapping("/update/{cartItemId}")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an item in the cart")
    public ResponseEntity<ApiResponse<Cart>> updateQuantity(
            @Parameter(description = "Cart item ID", required = true) @PathVariable Long cartItemId,
            @Parameter(description = "New quantity", required = true) @RequestParam int quantity) {
        Cart cart = cartService.updateQuantity(cartItemId, quantity);
        return new ResponseEntity<>(ApiResponse.success(cart, "Cart item quantity updated successfully"),
                HttpStatus.OK);
    }

    @DeleteMapping("/remove/{cartItemId}")
    @Operation(summary = "Remove item from cart", description = "Remove an item from the cart by its unique ID")
    public ResponseEntity<ApiResponse<Cart>> removeCartItem(
            @Parameter(description = "Cart item ID", required = true) @PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return new ResponseEntity<>(ApiResponse.success(null, "Cart item removed successfully"), HttpStatus.OK);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear the cart", description = "Clear all items from the cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(description = "Authorization token", required = true) @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }
        String username = authentication.getName();
        cartService.clearCart(username);
        return new ResponseEntity<>(ApiResponse.success(null, "Cart cleared successfully"), HttpStatus.OK);
    }

    @GetMapping("")
    @Operation(summary = "Get cart by ID", description = "Retrieve a cart by its unique ID")
    public ResponseEntity<ApiResponse<Cart>> getCartById(
            @Parameter(description = "Authorization token", required = true) @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }
        String username = authentication.getName();
        Cart cart = cartService.getCartUser(username);
        return new ResponseEntity<>(ApiResponse.success(cart, "Cart retrieved successfully"), HttpStatus.OK);
    }

}
