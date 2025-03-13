package datn.com.cosmetics.services;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.data.domain.Page;

import datn.com.cosmetics.bean.response.DiscountDTO;
import datn.com.cosmetics.entity.CartItem;
import datn.com.cosmetics.entity.Discount;

public interface IDiscountService {
    BigDecimal applyDiscountToCart(String discountCode, String username);

    BigDecimal calculateDiscount(Set<CartItem> cartItems, Discount discountCode);

    BigDecimal calculateApplicableAmount(Set<CartItem> cartItems, Long applicableProductId);

    Discount createDiscount(DiscountDTO discountDTO);

    Discount getDiscountById(Long id);

    Discount updateDiscount(Long id, DiscountDTO discountDTO);

    void deleteDiscount(Long id);

    Page<Discount> getAllDiscounts(String search, boolean isActive,int page, int size);

    void validateDiscountDTO(DiscountDTO discountDTO);

    void checkDuplicateDiscountName(String name);

    void checkDuplicateDiscountCode(String code);

    Page<Discount> searchDiscounts(String code, String name, int page, int size);
}
