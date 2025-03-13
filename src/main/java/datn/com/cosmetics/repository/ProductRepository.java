package datn.com.cosmetics.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByPriceBetweenAndBrandIdAndCategoryId(Double minPrice, Double maxPrice, Long brandId,
            Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
            + "(:brandId IS NULL OR p.brand.id = :brandId) AND "
            + "(:categoryId IS NULL OR p.category.id = :categoryId) AND "
            + "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND "
            + "(:isActive = FALSE OR (p.isActive = TRUE AND p.brand.isActive = TRUE AND p.category.isActive = TRUE))"
            + "ORDER BY "
            + "CASE WHEN :sortBy = 'name' AND :sortDirection = 'asc' THEN p.name END ASC, "
            + "CASE WHEN :sortBy = 'name' AND :sortDirection = 'desc' THEN p.name END DESC, "
            + "CASE WHEN :sortBy = 'createdDate' AND :sortDirection = 'asc' THEN p.createdDate END ASC, "
            + "CASE WHEN :sortBy = 'createdDate' AND :sortDirection = 'desc' THEN p.createdDate END DESC")
    Page<Product> findByFilters(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("brandId") Long brandId,
            @Param("categoryId") Long categoryId,
            @Param("name") String search,
            @Param("isActive") boolean isActive,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createdDate DESC")
    List<Product> findNewArrivals(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN OrderItem oi ON oi.product = p " +
            "GROUP BY p.id " +
            "HAVING SUM(oi.quantity) > 0 " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.isSale = true " +
            "ORDER BY (p.price - p.salePrice) / p.price DESC")
    List<Product> findTopDiscountedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.name LIKE %:keyword% " +
            "OR p.description LIKE %:keyword%" +
            " and p.isActive = true")
    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.product.id = :productId")
    int countOrderItemsByProductId(Long productId);
}
