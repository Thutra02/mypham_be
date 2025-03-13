package datn.com.cosmetics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    Review findByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT r FROM Review r WHERE r.orderItem.id = ?1 AND r.product.id = ?2")
    Review findByOrderIdAndProductId(Long orderItemId, Long productId);
}
