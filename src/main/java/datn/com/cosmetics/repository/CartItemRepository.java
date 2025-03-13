package datn.com.cosmetics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.CartItem;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // delete all cart item by cart id
    void deleteByCartId(Long cartId);
}
