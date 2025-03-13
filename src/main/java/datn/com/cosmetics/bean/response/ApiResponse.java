package datn.com.cosmetics.bean.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private Pagination pagination;

    // Constructor for success
    public ApiResponse(String status, String message, T data, Pagination pagination) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    // Static method to create a success response
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", message, data, null);
    }

    // Static method to create a success response with pagination
    public static <T> ApiResponse<T> success(T data, String message, Pagination pagination) {
        return new ApiResponse<>("success", message, data, pagination);
    }

    // Static method to create an error response
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message, null, null);
    }

    @Data
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;

        public Pagination(int currentPage, int totalPages, long totalItems) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }
    }
}