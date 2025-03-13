package datn.com.cosmetics.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.request.BrandRequest;
import datn.com.cosmetics.bean.response.BrandCategoryProductDTO;
import datn.com.cosmetics.entity.Brand;
import datn.com.cosmetics.exceptions.DuplicateResourceException;
import datn.com.cosmetics.exceptions.ResourceNotFoundException;
import datn.com.cosmetics.exceptions.ValidationException;
import datn.com.cosmetics.repository.BrandRepository;
import datn.com.cosmetics.services.IBrandService;
import datn.com.cosmetics.services.IUploadService;
import datn.com.cosmetics.utils.Extrac;
import jakarta.transaction.Transactional;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private IUploadService uploadService;

    @Override
    public Brand createBrand(BrandRequest brandRequest) {
        if (brandRepository.existsByName(brandRequest.getName())) {
            throw new DuplicateResourceException("Brand already exists with this name");
        }
        validateBrand(brandRequest);
        Brand brand = new Brand();
        brand.setName(brandRequest.getName());
        brand.setDescription(brandRequest.getDescription());
        brand.setImage(brandRequest.getImage());
        brand.setActive(brandRequest.isActive());
         return brandRepository.save(brand);
    }

    @Override
    public Brand updateBrand(Long id, BrandRequest brandRequest) {
        Optional<Brand> existingBrand = brandRepository.findById(id);
        if (existingBrand.isPresent()) {
            validateBrand(brandRequest);
            if (!brandRequest.getName().equals(existingBrand.get().getName())
                    && brandRepository.existsByName(brandRequest.getName())) {
                throw new DuplicateResourceException("Brand already exists with this name");
            }
            if (!brandRequest.getImage().equals(existingBrand.get().getImage())) {
                String imagePath = new Extrac().extractFilenameFromUrl(existingBrand.get().getImage());
                uploadService.deleteFile(imagePath);
            }
            Brand updatedBrand = existingBrand.get();
            updatedBrand.setName(brandRequest.getName());
            updatedBrand.setDescription(brandRequest.getDescription());
            updatedBrand.setImage(brandRequest.getImage());
            updatedBrand.setActive(brandRequest.isActive());
            return brandRepository.save(updatedBrand);
        }
        throw new ResourceNotFoundException("Brand not found");
    }

    private void validateBrand(BrandRequest brandRequest) {
        if (brandRequest.getName().isEmpty() || brandRequest.getDescription().isEmpty() ||
                brandRequest.getImage().isEmpty()  ) {
            throw new ValidationException("All fields are required");
        }
        // Add more validation logic as needed
    }

    @Transactional
    @Override
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found");
        }
         String imagePath = new Extrac().extractFilenameFromUrl(brandRepository.findById(id).get().getImage());
        uploadService.deleteFile(imagePath);
        brandRepository.deleteById(id);
    }

    @Override
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
    }
 

    @Override
    public Page<Brand> getAllBrands(String name,boolean isActive, Pageable pageable) {
        System.out.println("isActive: " + isActive);
        if (name != null && !name.isEmpty()) {
            return brandRepository.findByNameContainingIgnoreCase(name,isActive, pageable);
        }
        return brandRepository.findAll(isActive,pageable);
    }

    @Override
    public List<BrandCategoryProductDTO> getTop5Brands() {
        List<Object[]> results = brandRepository.getTop5BrandsWithCategoriesAndProductCount();
        List<BrandCategoryProductDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            BrandCategoryProductDTO dto = new BrandCategoryProductDTO();
            dto.setBrandId((Long) row[0]);
            dto.setBrandName((String) row[1]);
            dto.setCategoryId((Long) row[2]);
            dto.setCategoryName((String) row[3]);
            dto.setProductCount((Long) row[4]);
            dtos.add(dto);
        }
        return dtos;

    }
}
