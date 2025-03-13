package datn.com.cosmetics.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier of the category", example = "1")
  private Long id;

  @Schema(description = "Name of the category", example = "Skincare")
  private String name;

  @Schema(description = "Description of the category", example = "Products for skincare")
  private String description;

  @Schema(description = "Image URL of the category", example = "http://example.com/image.jpg")
  private String image;

   private boolean isActive = true;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Creation date of the category", example = "2023-10-01 12:00:00")
  private LocalDateTime createdDate = LocalDateTime.now();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @Schema(description = "Last update date of the category", example = "2023-10-01 12:00:00")
  private LocalDateTime updatedDate = LocalDateTime.now();

  @JsonIgnore
  @OneToMany(mappedBy = "category")
  @Schema(description = "List of products under this category")
  private List<Product> products;

  public Category(String name, String description, String image) {
    this.name = name;
    this.description = description;
    this.image = image;
  }

}