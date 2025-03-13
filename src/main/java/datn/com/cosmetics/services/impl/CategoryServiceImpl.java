package datn.com.cosmetics.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.request.CategoryRequest;
import datn.com.cosmetics.entity.Category;
import datn.com.cosmetics.exceptions.DuplicateCategoryNameException;
import datn.com.cosmetics.repository.CategoryRepository;
import datn.com.cosmetics.services.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryRequest categoryRequest) {
        checkDuplicateCategoryName(categoryRequest.getName());
        System.out.println(categoryRequest);
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setImage(categoryRequest.getImage());
        category.setActive(categoryRequest.isActive());
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        Optional<Category> existingCategoryOpt = categoryRepository.findById(id);

        if (existingCategoryOpt.isEmpty()) {
            return null; // Hoặc throw new NotFoundException("Category not found");
        }

        Category existingCategory = existingCategoryOpt.get();

        // Chỉ kiểm tra trùng lặp nếu người dùng thay đổi tên
        if (!existingCategory.getName().equals(categoryRequest.getName())) {
            checkDuplicateCategoryName(categoryRequest.getName());
        }
        System.out.println(categoryRequest);

        existingCategory.setName(categoryRequest.getName());
        existingCategory.setDescription(categoryRequest.getDescription());
        existingCategory.setImage(categoryRequest.getImage());
        existingCategory.setActive(categoryRequest.isActive());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Page<Category> getAllCategories(String name,boolean isActive, Pageable pageable) {
        if (name != null && !name.isEmpty()) {
            return categoryRepository.findByNameContaining(name,isActive, pageable);
        }
        return categoryRepository.findAll(isActive,pageable);
    }

    @Override
    public void checkDuplicateCategoryName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateCategoryNameException("Category name already exists: " + name);
        }
    }
}
