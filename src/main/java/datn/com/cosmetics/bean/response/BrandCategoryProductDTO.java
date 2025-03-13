package datn.com.cosmetics.bean.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandCategoryProductDTO {
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Long productCount;
}