package datn.com.cosmetics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.BlogRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Blog;
import datn.com.cosmetics.services.IBlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/blog")
@Tag(name = "Blog", description = "Blog management APIs")
public class BlogController {

    @Autowired
    private IBlogService blogService;

    @PostMapping
    @Operation(summary = "Create a new blog", description = "Create a new blog with the provided details")
    public ResponseEntity<ApiResponse<Blog>> createBlog(
            @RequestHeader(name = "Authorization", required = true) String jwt,
            @RequestBody BlogRequest blogRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
            }
            System.out.println("jwt: " + jwt);
            String username = authentication.getName();
            blogRequest.setAuthor(username);
            Blog createdBlog = blogService.createBlog(blogRequest);
            return ResponseEntity.ok(ApiResponse.success(createdBlog, "Blog created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error creating blog: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing blog", description = "Update the details of an existing blog by ID")
    public ResponseEntity<ApiResponse<Blog>> updateBlog(@PathVariable Long id,
            @RequestBody BlogRequest blogRequest) {
        try {
            Blog updatedBlog = blogService.updateBlog(id, blogRequest);
            if (updatedBlog != null) {
                return ResponseEntity.ok(ApiResponse.success(updatedBlog, "Blog updated successfully"));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Blog not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error updating blog: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blog", description = "Delete a blog by ID")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable Long id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Blog deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error deleting blog: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get blog by ID", description = "Retrieve a blog by its ID")
    public ResponseEntity<ApiResponse<Blog>> getBlogById(@PathVariable Long id) {
        try {
            Blog blog = blogService.getBlogById(id);
            if (blog != null) {
                return ResponseEntity.ok(ApiResponse.success(blog, "Blog retrieved successfully"));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Blog not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving blog: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all blogs", description = "Retrieve a list of all blogs with optional search by title and pagination")
    public ResponseEntity<ApiResponse<List<Blog>>> getAllBlogs(
            @Parameter(description = "Search", required = false) @RequestParam(required = false) String search,
            Pageable pageable) {
        try {
            Page<Blog> blogs = blogService.getAllBlogs(pageable, search);
            ApiResponse.Pagination pagination = new ApiResponse.Pagination(blogs.getNumber() + 1,
                    blogs.getTotalPages(), blogs.getTotalElements());
            String message = "Blogs retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(blogs.getContent(), message, pagination));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving blogs: " + e.getMessage()));
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "Get top 4 latest blogs", description = "Retrieve the top 4 latest blogs")
    public ResponseEntity<ApiResponse<List<Blog>>> getTop4LatestBlogs() {
        try {
            List<Blog> blogs = blogService.getTop4LatestBlogs();
            return ResponseEntity.ok(ApiResponse.success(blogs, "Top 4 latest blogs retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving latest blogs: " + e.getMessage()));
        }
    }
}
