package datn.com.cosmetics.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.bean.request.ReviewRequest;
import datn.com.cosmetics.entity.OrderItem;
import datn.com.cosmetics.entity.Product;
import datn.com.cosmetics.entity.Review;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.repository.OrderItemRepository;
import datn.com.cosmetics.repository.ProductRepository;
import datn.com.cosmetics.repository.ReviewRepository;
import datn.com.cosmetics.repository.UserRepository;
import datn.com.cosmetics.services.IReviewService;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Review addReview(ReviewRequest reviewRequest, String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        OrderItem orderItem = orderItemRepository.getOrderItemById(reviewRequest.getOrderId(), reviewRequest.getProductId());
        if (orderItem == null) {
            throw new RuntimeException("Order item not found");
        }
        System.out.println(orderItem.getProduct().getId());
      

        if (orderItem.getOrder().getUser().getId() != user.getId()) {
            throw new RuntimeException("Order item does not belong to user");
        }

        if (reviewRepository.findByOrderIdAndProductId(orderItem.getId(), reviewRequest.getProductId()) != null) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
        }

        Review review = new Review();
        review.setReview(reviewRequest.getReview());
        review.setStar(reviewRequest.getStar());
        review.setProduct(productRepository.findById(reviewRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")));
        review.setUser(user);
        review.setOrderItem(orderItem);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return reviewRepository.findByProductId(product.getId());
    }

}
