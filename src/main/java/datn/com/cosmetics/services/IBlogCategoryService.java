package datn.com.cosmetics.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.BlogCategoryRequest;
import datn.com.cosmetics.bean.response.BlogCategoryDTO;
import datn.com.cosmetics.entity.BlogCategory;

public interface IBlogCategoryService {
    BlogCategory createBlogCategory(BlogCategoryRequest request);
    BlogCategory getBlogCategoryById(Long id);
    Page<BlogCategory> getAllBlogCategories(Pageable pageable, String name);
    BlogCategory updateBlogCategory(Long id, BlogCategoryRequest request);
    void deleteBlogCategory(Long id);
    public List<BlogCategoryDTO> getAllCategoriesWithBlogCount() ;
}
