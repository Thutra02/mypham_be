package datn.com.cosmetics.services;

import java.util.List;

import datn.com.cosmetics.bean.request.ReviewRequest;
import datn.com.cosmetics.entity.Review;

public interface IReviewService {
    Review addReview(ReviewRequest reviewRequest, String username);

    List<Review> getReviewByProductId(Long productId);
}
