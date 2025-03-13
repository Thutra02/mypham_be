package datn.com.cosmetics.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND" +
    "(:isActive = FALSE OR c.isActive = TRUE)"
    )
    Page<Category> findByNameContaining(String name,boolean isActive ,Pageable pageable);
    boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE " +
            "(:isActive = FALSE OR c.isActive = TRUE)")
    Page<Category> findAll(boolean isActive, Pageable pageable);
}
