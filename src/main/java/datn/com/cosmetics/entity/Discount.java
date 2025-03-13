
package datn.com.cosmetics.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import datn.com.cosmetics.entity.enums.DiscountType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String discountCode;
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;
    private Integer maxUsage;
    private Integer usageCount =0;
    private Long applicableProductId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;

    public Discount(
            String name,
            String discountCode,
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal minOrderValue,
            BigDecimal maxDiscountAmount,
            Integer maxUsage,
            Integer usageCount,
            Long applicableProductId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isActive) {
        this.name = name;
        this.discountCode = discountCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderValue = minOrderValue;
        this.maxDiscountAmount = maxDiscountAmount;
        this.maxUsage = maxUsage;
        this.usageCount = usageCount;
        this.applicableProductId = applicableProductId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;

    }
}
