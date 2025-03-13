package datn.com.cosmetics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.entity.Tag;

public interface ITagService {
    
    Tag createTag(Tag tag);
    Tag updateTag(Long id, Tag tag);
    void deleteTag(Long id);
    Page<Tag> getAllTags(String name, Pageable pageable);
    Tag getTagById(Long id);
 }
