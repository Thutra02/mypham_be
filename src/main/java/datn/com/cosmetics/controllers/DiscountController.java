package datn.com.cosmetics.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.DiscountRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.bean.response.DiscountDTO;
import datn.com.cosmetics.entity.Discount;
import datn.com.cosmetics.exceptions.DuplicateDiscountCodeException;
import datn.com.cosmetics.exceptions.DuplicateDiscountNameException;
import datn.com.cosmetics.exceptions.ValidationException;
import datn.com.cosmetics.services.IDiscountService;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private IDiscountService discountService;

    @PostMapping
    public ResponseEntity<ApiResponse<Discount>> createDiscount(@RequestBody DiscountDTO discountDTO) {
        try {
            discountService.validateDiscountDTO(discountDTO);
            discountService.checkDuplicateDiscountName(discountDTO.getName());
            discountService.checkDuplicateDiscountCode(discountDTO.getDiscountCode());
            Discount createdDiscount = discountService.createDiscount(discountDTO);
            return ResponseEntity.ok(ApiResponse.success(createdDiscount, "Discount created successfully"));
        } catch (ValidationException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (DuplicateDiscountNameException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Duplicate name error: " + e.getMessage()));
        } catch (DuplicateDiscountCodeException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Duplicate code error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Discount>> getDiscountById(@PathVariable Long id) {
        try {
            Discount discountDTO = discountService.getDiscountById(id);
            return ResponseEntity.ok(ApiResponse.success(discountDTO, "Discount retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("Discount not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Discount>> updateDiscount(@PathVariable Long id,
            @RequestBody DiscountDTO discountDTO) {
        try {
            Discount updatedDiscount = discountService.updateDiscount(id, discountDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedDiscount, "Discount updated successfully"));
        } catch (ValidationException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Validation error: " + e.getMessage()));
        } catch (DuplicateDiscountNameException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Duplicate name error: " + e.getMessage()));
        } catch (DuplicateDiscountCodeException e) {
            return ResponseEntity.status(400).body(ApiResponse.error("Duplicate code error: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error("Discount not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDiscount(@PathVariable Long id) {

        discountService.deleteDiscount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Discount deleted successfully"));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Discount>>> getAllDiscounts(@RequestParam(required = false) String search,
            @RequestParam(required = false) boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Discount> discounts = discountService.getAllDiscounts(search, isActive, page, size);
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(discounts.getNumber() + 1,
                    discounts.getTotalPages(),
                    discounts.getTotalElements());
            String message = "Discounts retrieved successfully";

            return ResponseEntity.ok(ApiResponse.success(discounts.getContent(), message, pagination));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<BigDecimal>> applyDiscount(
            @RequestHeader("Authorization") String token,
            @RequestBody DiscountRequest discountCode) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
            }

            String username = authentication.getName();
            BigDecimal appliedDiscount = discountService.applyDiscountToCart(discountCode.getCode(), username);
            return ResponseEntity.ok(ApiResponse.success(appliedDiscount, "Discount applied successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(ApiResponse.error( e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Discount>>> searchDiscounts(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Discount> discounts = discountService.searchDiscounts(code, name, page, size);
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(discounts.getNumber() + 1,
                    discounts.getTotalPages(),
                    discounts.getTotalElements());
            String message = "Discounts retrieved successfully";

            return ResponseEntity.ok(ApiResponse.success(discounts.getContent(), message, pagination));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}
