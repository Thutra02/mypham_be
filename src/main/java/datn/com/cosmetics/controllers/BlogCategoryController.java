package datn.com.cosmetics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.BlogCategoryRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.bean.response.BlogCategoryDTO;
import datn.com.cosmetics.entity.BlogCategory;
import datn.com.cosmetics.services.IBlogCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/blog-category")
@Tag(name = "Blog Category", description = "API for blog category management")
public class BlogCategoryController {

    @Autowired
    private IBlogCategoryService blogCategoryService;

    @PostMapping
    @Operation(summary = "Create a new blog category", description = "Create a new blog category with the provided details")
    public ResponseEntity<ApiResponse<BlogCategory>> createBlogCategory(
            @Parameter(description = "Blog category request body", required = true) @RequestBody BlogCategoryRequest request) {
        BlogCategory blogCategory = blogCategoryService.createBlogCategory(request);
        return new ResponseEntity<>(ApiResponse.success(blogCategory, "Blog category created successfully"),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a blog category by ID", description = "Retrieve a blog category by its unique ID")
    public ResponseEntity<ApiResponse<BlogCategory>> getBlogCategoryById(
            @Parameter(description = "Blog category ID", required = true) @PathVariable Long id) {
        BlogCategory blogCategory = blogCategoryService.getBlogCategoryById(id);
        return new ResponseEntity<>(ApiResponse.success(blogCategory, "Blog category retrieved successfully"),
                HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all blog categories", description = "Retrieve all blog categories with pagination and optional name filter")
    public ResponseEntity<ApiResponse<List<BlogCategory>>> getAllBlogCategories(
            @Parameter(description = "Page number", required = false) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false) @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Blog category name filter", required = false) @RequestParam(required = false) String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogCategory> blogCategories = blogCategoryService.getAllBlogCategories(pageable, name);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(blogCategories.getNumber() + 1,
                blogCategories.getTotalPages(), blogCategories.getTotalElements());
        return new ResponseEntity<>(

                ApiResponse.success(blogCategories.getContent(), "Blog categories retrieved successfully", pagination),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a blog category", description = "Update the details of an existing blog category by its ID")
    public ResponseEntity<ApiResponse<BlogCategory>> updateBlogCategory(
            @Parameter(description = "Blog category ID", required = true) @PathVariable Long id,
            @Parameter(description = "Blog category request body", required = true) @RequestBody BlogCategoryRequest request) {
        BlogCategory blogCategory = blogCategoryService.updateBlogCategory(id, request);
        return new ResponseEntity<>(ApiResponse.success(blogCategory, "Blog category updated successfully"),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blog category", description = "Delete a blog category by its unique ID")
    public ResponseEntity<ApiResponse<Void>> deleteBlogCategory(
            @Parameter(description = "Blog category ID", required = true) @PathVariable Long id) {
        blogCategoryService.deleteBlogCategory(id);
        return new ResponseEntity<>(ApiResponse.success(null, "Blog category deleted successfully"),
                HttpStatus.NO_CONTENT);
    }

    @GetMapping("/blog-count")
    public ResponseEntity<List<BlogCategoryDTO>> getCategoriesWithBlogCount() {
        List<BlogCategoryDTO> categories = blogCategoryService.getAllCategoriesWithBlogCount();
        return ResponseEntity.ok(categories);
    }
}
