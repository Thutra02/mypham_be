package datn.com.cosmetics.bean.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import datn.com.cosmetics.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDTO {
    private String name;
    private String discountCode;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private DiscountType discountType;
    private Integer maxUsage;
    private BigDecimal maxDiscountAmount;
    private Long applicableProductId;
    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
