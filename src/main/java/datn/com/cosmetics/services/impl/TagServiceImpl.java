package datn.com.cosmetics.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.entity.Tag;
import datn.com.cosmetics.repository.TagRepository;
import datn.com.cosmetics.services.ITagService;

@Service
public class TagServiceImpl implements ITagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag createTag(Tag tag) {
        Tag existedTag = tagRepository.findByName(tag.getName());
        if (existedTag != null) {
            throw new RuntimeException("Tag name is already existed");
        }
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {
        if (tagRepository.findById(id).isPresent()) {
            return tagRepository.save(tag);
        }
        throw new RuntimeException("Tag not found");
    }

    @Override
    public void deleteTag(Long id) {
        if (tagRepository.findById(id).isPresent()) {
            tagRepository.deleteById(id);
        } else {
            throw new RuntimeException("Tag not found");
        }
    }

    @Override
    public Page<Tag> getAllTags(String name, Pageable pageable) {
        if (name == null) {
            return tagRepository.findByNameContaining(name, pageable);
        }
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag getTagById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

}
