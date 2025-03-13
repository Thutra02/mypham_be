package datn.com.cosmetics.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d FROM Discount d WHERE d.discountCode = :discountCode and d.isActive = TRUE")
    Discount findByDiscountCode(String discountCode);

    @Query("SELECT d FROM Discount d WHERE " +
            "(LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.discountCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND" +
            "(:isActive = FALSE OR d.isActive = TRUE)")
    Page<Discount> findByDiscountCodeContainingIgnoreCase(String search,boolean isActive, Pageable pageable);

    Page<Discount> findByDiscountCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String code, String name, Pageable pageable);

    boolean existsByDiscountCode(String discountCode);

    boolean existsByName(String discountName);
}