package datn.com.cosmetics.bean.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CategoryRequest {

    @Schema(description = "Name of the category", example = "Skincare")
    private String name;

    @Schema(description = "Description of the category", example = "Products for skincare")
    private String description;

    @Schema(description = "Image URL of the category", example = "http://example.com/image.jpg")
    private String image;

    private boolean isActive  ;
 
}
