package datn.com.cosmetics.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.bean.response.BlogCategoryDTO;
import datn.com.cosmetics.entity.BlogCategory;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {
    Page<BlogCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT new datn.com.cosmetics.bean.response.BlogCategoryDTO(c.id, c.name, c.description, COUNT(b)) " +
            "FROM BlogCategory c LEFT JOIN Blog b ON c.id = b.category.id " +
            "GROUP BY c.id, c.name, c.description " +
            "ORDER BY COUNT(b) DESC")
    List<BlogCategoryDTO> findTop5CategoriesWithBlogCount(Pageable pageable);

}
