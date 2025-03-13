package datn.com.cosmetics.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.BrandRequest;
import datn.com.cosmetics.bean.response.BrandCategoryProductDTO;
import datn.com.cosmetics.entity.Brand;

public interface IBrandService {
    Brand createBrand(BrandRequest brand);

    Brand updateBrand(Long id, BrandRequest brand);

    void deleteBrand(Long id);

    Brand getBrandById(Long id);

 
    Page<Brand> getAllBrands(String name,boolean isActive, Pageable pageable);

  public List<BrandCategoryProductDTO> getTop5Brands() ;
}
