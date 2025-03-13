package datn.com.cosmetics.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.ProductRequest;
import datn.com.cosmetics.entity.Product;

public interface IProductService {
        Product createProduct(ProductRequest productRequest);

        Product updateProduct(Long id, ProductRequest productRequest);

        void deleteProduct(Long id);

        Product getProductById(Long id);

        Page<Product> getAllProducts(Double minPrice, Double maxPrice, Long brandId, Long categoryId, String sortBy,
                        String sortDirection, Pageable pageable,String search, boolean isActive);

        Page<Product> getProductsByFilter(Double minPrice, Double maxPrice, Long brandId, Long categoryId,
                        Pageable pageable);

        List<Product> getNewArrivals();

        List<Product> getTopSellingProducts();

        List<Product> getTopDiscountedProducts();

        // search
        List<Product> searchProducts(String keyword);
}
