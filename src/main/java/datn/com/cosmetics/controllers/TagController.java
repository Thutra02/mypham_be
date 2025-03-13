package datn.com.cosmetics.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Tag;
import datn.com.cosmetics.services.ITagService;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private ITagService tagService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllTags(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<Tag> tags = tagService.getAllTags(search, pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(tags.getNumber(), tags.getTotalPages(),
                tags.getTotalElements());
        String message = tags.isEmpty() ? "No tags found" : "Tags retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(tags.getContent(), message, pagination));
    }

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<Tag>> getTagById(Long id) {
        Tag tag = tagService.getTagById(id);
        if (tag != null) {
            return ResponseEntity.ok(ApiResponse.success(tag, "Tag retrieved successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Tag not found"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tag>> getTagsByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getTagById(id), "Tags retrieved successfully"));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Tag>> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ResponseEntity.ok(ApiResponse.success(createdTag, "Tag created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tag>> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        Tag updatedTag = tagService.updateTag(id, tag);
        if (updatedTag != null) {
            return ResponseEntity.ok(ApiResponse.success(updatedTag, "Tag updated successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Tag not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Tag deleted successfully"));
    }
}
