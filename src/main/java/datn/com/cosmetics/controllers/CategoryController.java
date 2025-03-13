package datn.com.cosmetics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import datn.com.cosmetics.bean.request.CategoryRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Category;
import datn.com.cosmetics.services.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/category")
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a new category with the provided details")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody CategoryRequest categoryRequest) {
        Category createdCategory = categoryService.createCategory(categoryRequest);
        return ResponseEntity.ok(ApiResponse.success(createdCategory, "Category created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category", description = "Update the details of an existing category by ID")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
            @RequestBody CategoryRequest categoryRequest) {
        Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
        if (updatedCategory != null) {
            return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category updated successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Category not found"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Delete a category by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a category by its ID")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            return ResponseEntity.ok(ApiResponse.success(category, "Category retrieved successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Category not found"));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories with optional search by name and pagination")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories(
            @Parameter(description = "Search", required = false) @RequestParam(required = false) String search,
            @Parameter(description = "Is active", required = false) @RequestParam(required = false)  boolean isActive,

            Pageable pageable) {
        Page<Category> categories = categoryService.getAllCategories(search, isActive,pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(categories.getNumber() + 1,
                categories.getTotalPages(), categories.getTotalElements());
        String message = "Categories retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(categories.getContent(), message, pagination));
    }
}
