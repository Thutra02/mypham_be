package datn.com.cosmetics.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.request.BlogCategoryRequest;
import datn.com.cosmetics.bean.response.BlogCategoryDTO;
import datn.com.cosmetics.entity.BlogCategory;
import datn.com.cosmetics.repository.BlogCategoryRepository;
import datn.com.cosmetics.services.IBlogCategoryService;
import jakarta.transaction.Transactional;

@Service
public class BlogCategoryServiceImpl implements IBlogCategoryService {

    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @Override
    public BlogCategory createBlogCategory(BlogCategoryRequest request) {
        BlogCategory blogCategory = new BlogCategory();
        blogCategory.setName(request.getName());
        blogCategory.setDescription(request.getDescription());
        return blogCategoryRepository.save(blogCategory);
    }

    @Override
    public BlogCategory getBlogCategoryById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog category not found"));
    }

    @Transactional
    @Override
    public Page<BlogCategory> getAllBlogCategories(Pageable pageable, String name) {
        if (name != null && !name.isEmpty()) {
            return blogCategoryRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        return blogCategoryRepository.findAll(pageable);
    }

    @Override
    public BlogCategory updateBlogCategory(Long id, BlogCategoryRequest request) {
        BlogCategory blogCategory = getBlogCategoryById(id);
        blogCategory.setName(request.getName());
        blogCategory.setDescription(request.getDescription());
        return blogCategoryRepository.save(blogCategory);
    }

    @Override
    public void deleteBlogCategory(Long id) {
        blogCategoryRepository.deleteById(id);
    }

    @Override
    public List<BlogCategoryDTO> getAllCategoriesWithBlogCount() {
        Pageable pageable = PageRequest.of(0, 5);
        List<BlogCategoryDTO> top5Categories = blogCategoryRepository.findTop5CategoriesWithBlogCount(pageable);
        return top5Categories;
    }
}
