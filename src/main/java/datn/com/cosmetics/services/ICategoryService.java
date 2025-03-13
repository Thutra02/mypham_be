package datn.com.cosmetics.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.CategoryRequest;
import datn.com.cosmetics.entity.Category;

public interface ICategoryService {
    Category createCategory(CategoryRequest categoryRequest);
    Category updateCategory(Long id, CategoryRequest categoryRequest);
    void deleteCategory(Long id);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    Page<Category> getAllCategories(String name, boolean isActive, Pageable pageable);
    void checkDuplicateCategoryName(String name);
}
