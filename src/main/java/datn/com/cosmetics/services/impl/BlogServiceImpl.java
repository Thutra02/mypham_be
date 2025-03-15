package datn.com.cosmetics.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.request.BlogRequest;
import datn.com.cosmetics.entity.Blog;
import datn.com.cosmetics.entity.BlogCategory;
import datn.com.cosmetics.entity.Tag;
import datn.com.cosmetics.repository.BlogCategoryRepository;
import datn.com.cosmetics.repository.BlogRepository;
import datn.com.cosmetics.repository.TagRepository;
import datn.com.cosmetics.repository.UserRepository;
import datn.com.cosmetics.services.IBlogService;

@Service
public class BlogServiceImpl implements IBlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogCategoryRepository blogCategoryRepository;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Blog createBlog(BlogRequest request) {
        try {
            System.out.println(request.getTags());
            Blog blog = new Blog();
            blog.setTitle(request.getTitle());
            blog.setContent(request.getContent());
            blog.setImage(request.getImage());
            blog.setStatus(request.getStatus());
            BlogCategory category = blogCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            blog.setCategory(category);
            blog.setAuthor(userRepository.findByEmail(request.getAuthor()));
            Set<Tag> tags = new HashSet<>();
            if (request.getTags() != null) {
                for (String tagName : request.getTags()) {
                    Tag tag = tagRepository.findByName(tagName);
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tag = tagRepository.save(tag);
                    }
                    tags.add(tag);
                }
            }
            blog.setTags(tags);

            return blogRepository.save(blog);
        } catch (Exception e) {
            throw new RuntimeException("Error creating blog: " + e.getMessage());
        }
    }


    @Override
    public Blog updateBlog(Long id, BlogRequest request) {
        try {
            System.out.println(request.getTags());
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blog not found"));

            blog.setTitle(request.getTitle());
            blog.setContent(request.getContent());
            blog.setImage(request.getImage());
            blog.setStatus(request.getStatus());

            BlogCategory category = blogCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            blog.setCategory(category);

            Set<Tag> tags = new HashSet<>();
            if (request.getTags() != null) {
                for (String tagName : request.getTags()) {
                    Tag tag = tagRepository.findByName(tagName);
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tag = tagRepository.save(tag);
                    }
                    tags.add(tag);
                }
            }
            blog.setTags(tags);

            return blogRepository.save(blog);
        } catch (Exception e) {
            throw new RuntimeException("Error updating blog: " + e.getMessage());
        }
    }


    @Override
    public Blog getBlogById(Long id) {
        try {
            return blogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blog not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving blog: " + e.getMessage());
        }
    }

    @Override
    public Page<Blog> getAllBlogs(Pageable pageable, String title) {
        try {
            if (title != null && !title.isEmpty()) {
                return blogRepository.findByTitleContaining(pageable, title);
            }
            return blogRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving blogs: " + e.getMessage());
        }
    }

    @Override
    public void deleteBlog(Long id) {
        try {
            blogRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting blog: " + e.getMessage());
        }
    }

    @Override
    public List<Blog> getTop4LatestBlogs() {
        try {
            Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdDate"));
            return blogRepository.findAll(pageable).getContent();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving latest blogs: " + e.getMessage());
        }
    }
}
