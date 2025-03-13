package datn.com.cosmetics.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.bean.request.BlogRequest;
import datn.com.cosmetics.entity.Blog;

public interface IBlogService {
    Blog createBlog(BlogRequest request);
    Blog getBlogById(Long id);
    Page<Blog> getAllBlogs(Pageable pageable, String title);
    Blog updateBlog(Long id, BlogRequest request);
    void deleteBlog(Long id);
    List<Blog> getTop4LatestBlogs();
}
