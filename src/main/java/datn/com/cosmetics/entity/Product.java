package datn.com.cosmetics.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  @Lob
  @Column(columnDefinition = "TEXT")
  private String description;

  private BigDecimal price;
  private BigDecimal salePrice;
  private boolean isSale;
  private int stock;
@Lob
  @Column(columnDefinition = "TEXT")
  private String ingredients;
  @Lob
  @Column(columnDefinition = "TEXT")
  private String productUsage;
  @JsonManagedReference
  @OneToMany(mappedBy = "product")
  private List<ProductImage> productImages;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "brand_id")
  private Brand brand;
  @JsonManagedReference
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews = new ArrayList<>();

  private boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

  public Product(Long id, String name, String description, BigDecimal price, BigDecimal salePrice, boolean isSale,
      int stock, String ingredients, String productUsage, List<ProductImage> productImages, Category category,
      Brand brand, List<Review> reviews, String status, LocalDateTime createdDate, LocalDateTime updatedDate,
      BigDecimal discountPercentage) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.salePrice = salePrice;
    this.isSale = isSale;
    this.stock = stock;
    this.ingredients = ingredients;
    this.productUsage = productUsage;
    this.productImages = productImages;
    this.category = category;
    this.brand = brand;
    this.reviews = reviews;
     this.createdDate = createdDate;
    this.updatedDate = updatedDate;
  }

}