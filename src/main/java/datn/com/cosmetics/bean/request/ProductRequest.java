package datn.com.cosmetics.bean.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isSale;
    private BigDecimal salePrice;
    private int stock;
    private String ingredients;
    private String productUsage;
    private Long categoryId;
    private Long brandId;
    private boolean active;
    private List<String> images;
}
