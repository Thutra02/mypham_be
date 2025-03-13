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

import datn.com.cosmetics.bean.request.BrandRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.bean.response.BrandCategoryProductDTO;
import datn.com.cosmetics.entity.Brand;
import datn.com.cosmetics.exceptions.ValidationException;
import datn.com.cosmetics.services.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brand")
@Tag(name = "Brand", description = "API for brand management")
public class BrandController {

    @Autowired
    private IBrandService brandService;
   

    @PostMapping
    @Operation(summary = "Create a new brand", description = "Create a new brand with the provided details")
    public ResponseEntity<ApiResponse<Brand>> createBrand(@Valid @RequestBody BrandRequest brandRequest) {
        try {
            Brand createdBrand = brandService.createBrand(brandRequest);
            return ResponseEntity.ok(ApiResponse.success(createdBrand, "Brand created successfully"));
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing brand", description = "Update the details of an existing brand by ID")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(@PathVariable Long id,
            @Valid @RequestBody BrandRequest brandRequest) {
        try {
            Brand updatedBrand = brandService.updateBrand(id, brandRequest);
            if (updatedBrand != null) {
                return ResponseEntity.ok(ApiResponse.success(updatedBrand, "Brand updated successfully"));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Brand not found"));
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a brand", description = "Delete a brand by ID")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Brand deleted successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID", description = "Retrieve a brand by its ID")
    public ResponseEntity<ApiResponse<Brand>> getBrandById(@PathVariable Long id) {
        Brand brand = brandService.getBrandById(id);
        if (brand != null) {
            return ResponseEntity.ok(ApiResponse.success(brand, "Brand retrieved successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Brand not found"));
    }

    @GetMapping
    @Operation(summary = "Get all brands", description = "Retrieve a list of all brands with optional search by name and pagination")
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands(@RequestParam(required = false) String search,
    @RequestParam(required = false) boolean isActive,
            Pageable pageable) {
        Page<Brand> brands = brandService.getAllBrands(search,isActive, pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(brands.getNumber() + 1, brands.getTotalPages(),
                brands.getTotalElements());
        String message = "Brands retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(brands.getContent(), message, pagination));
    }

    @GetMapping("/top5")
    public ResponseEntity<ApiResponse<List<BrandCategoryProductDTO>>> getTop5Brands() {
        List<BrandCategoryProductDTO> brands = brandService.getTop5Brands();
        return ResponseEntity.ok(ApiResponse.success(brands, "Top 5 brands retrieved successfully"));
    }

}
