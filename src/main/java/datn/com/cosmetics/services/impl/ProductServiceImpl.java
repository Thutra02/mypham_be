package datn.com.cosmetics.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import datn.com.cosmetics.bean.request.ProductRequest;
import datn.com.cosmetics.entity.Brand;
import datn.com.cosmetics.entity.Category;
import datn.com.cosmetics.entity.Product;
import datn.com.cosmetics.entity.ProductImage;
import datn.com.cosmetics.repository.BrandRepository;
import datn.com.cosmetics.repository.CategoryRepository;
import datn.com.cosmetics.repository.ProductImageRepository;
import datn.com.cosmetics.repository.ProductRepository;
import datn.com.cosmetics.services.IProductService;
import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductRequest productRequest) {
        try {
            validateProductRequest(productRequest);
            Product product = new Product();
            mapProductRequestToProduct(productRequest, product);
            product = productRepository.save(product);
            saveProductImages(productRequest, product);
            return product;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public Product updateProduct(Long id, ProductRequest productRequest) {
        try {
            System.out.println("ProductRequest: " + productRequest);
            validateProductRequest(productRequest);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            mapProductRequestToProduct(productRequest, product);
            updateProductImages(productRequest, product);
            productRepository.save(product);
            // Refresh the product entity to ensure updated data is returned
            product = productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            return product;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        
            int count = productRepository.countOrderItemsByProductId(id);
            if (count > 0) {
                throw new IllegalStateException("Sản phẩm đang có trong đơn hàng, không thể xóa!");
            }
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
                    // xóa ảnh
            productImageRepository.deleteByProductId(product.getId());
            productRepository.deleteById(id);
       
    }
    
    @Override
    public Product getProductById(Long id) {
        try {
            return productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public Page<Product> getAllProducts(Double minPrice, Double maxPrice, Long brandId, Long categoryId, String sortBy,
            String sortDirection, Pageable pageable, String search, boolean isActive) {
        try {
            if (minPrice != null || maxPrice != null || brandId != null || categoryId != null || sortBy != null
                    || sortDirection != null || search != null) {

                // Nếu sortDirection là null, đặt mặc định là "asc"
                Sort.Direction direction = Sort.Direction.ASC; // Giá trị mặc định
                if (sortDirection != null) {
                    direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                }

                // Nếu sortBy không hợp lệ, mặc định sắp xếp theo "name"
                List<String> allowedSortFields = Arrays.asList("name", "createdDate", "price");
                if (sortBy == null || !allowedSortFields.contains(sortBy)) {
                    sortBy = "name";
                }
                if (sortDirection == null) {
                    sortDirection = "asc";
                }

                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, sortBy));
                return productRepository.findByFilters(minPrice, maxPrice, brandId, categoryId, search, isActive,
                        sortBy, sortDirection,
                        pageable);
            }
            return productRepository.findAll(pageable);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lỗi khi lấy danh sách sản phẩm: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<Product> getProductsByFilter(Double minPrice, Double maxPrice, Long brandId, Long categoryId,
            Pageable pageable) {
        return productRepository.findByPriceBetweenAndBrandIdAndCategoryId(minPrice, maxPrice, brandId, categoryId,
                pageable);
    }

    @Override
    public List<Product> getNewArrivals() {
        List<Product> newArrivals = productRepository.findNewArrivals(PageRequest.of(0, 10));
        return newArrivals;
    }

    @Override
    public List<Product> getTopSellingProducts() {
        List<Product> topSelling = productRepository.findTopSellingProducts(PageRequest.of(0, 10));
        return topSelling;
    }

    @Override
    public List<Product> getTopDiscountedProducts() {
        List<Product> topDiscounted = productRepository.findTopDiscountedProducts(PageRequest.of(0, 10));
        return topDiscounted;
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    private void mapProductRequestToProduct(ProductRequest productRequest, Product product) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setSalePrice(productRequest.getSalePrice());
        product.setStock(productRequest.getStock());
        product.setIngredients(productRequest.getIngredients());
        product.setProductUsage(productRequest.getProductUsage());
        product.setActive(productRequest.isActive());

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);

        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        product.setBrand(brand);

    }

    private void validateProductRequest(ProductRequest productRequest) {
        if (productRequest.getName() == null || productRequest.getName().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
       
        if (productRequest.isSale() && productRequest.getSalePrice() == null) {
            throw new RuntimeException("Product price must be greater than zero");
        }
        if (productRequest.getCategoryId() == null) {
            throw new RuntimeException("Category ID is required");
        }
        if (productRequest.getBrandId() == null) {
            throw new RuntimeException("Brand ID is required");
        }
    }

    private void saveProductImages(ProductRequest productRequest, Product product) {
        if (productRequest.getImages() != null && !productRequest.getImages().isEmpty()) {
            for (String imageUrl : productRequest.getImages()) {
                ProductImage productImage = new ProductImage();
                productImage.setImage(imageUrl);
                productImage.setProduct(product);
                productImageRepository.save(productImage);
            }
        }
    }

    private void updateProductImages(ProductRequest productRequest, Product product) {
        // Xóa tất cả ảnh cũ của sản phẩm
        productImageRepository.deleteByProductId(product.getId());

        // Thêm danh sách ảnh mới từ request
        if (productRequest.getImages() != null && !productRequest.getImages().isEmpty()) {
            for (String imageUrl : productRequest.getImages()) {
                ProductImage productImage = new ProductImage();
                productImage.setImage(imageUrl);
                productImage.setProduct(product);
                productImageRepository.save(productImage);
            }
        }
    }

}
