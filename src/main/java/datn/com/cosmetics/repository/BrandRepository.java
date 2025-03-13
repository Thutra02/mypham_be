package datn.com.cosmetics.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

        @Query("SELECT b FROM Brand b WHERE b.name LIKE %:name%  AND " +
                        "(:isActive = FALSE OR b.isActive = TRUE)")
        Page<Brand> findByNameContainingIgnoreCase(String name, boolean isActive, Pageable pageable);

        @Query("SELECT b FROM Brand b WHERE " +
                        "(:isActive = FALSE OR b.isActive = TRUE)")     
        Page<Brand> findAll(boolean isActive, Pageable pageable);

        Brand findByName(String name);

        boolean existsByName(String name);

        @Query("SELECT b.id AS brandId, b.name AS brandName, " +
                        "c.id AS categoryId, c.name AS categoryName, " +
                        "COUNT(p.id) AS productCount " +
                        "FROM Product p " +
                        "JOIN p.brand b " +
                        "JOIN p.category c " +
                        "WHERE p.isActive = TRUE " + // Thêm điều kiện lọc sản phẩm active
                        "GROUP BY b.id, b.name, c.id, c.name " +
                        "ORDER BY COUNT(p.id) DESC")
        List<Object[]> getTop5BrandsWithCategoriesAndProductCount();

}
