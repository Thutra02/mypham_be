package datn.com.cosmetics.bean.response;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long blogCount;
}
