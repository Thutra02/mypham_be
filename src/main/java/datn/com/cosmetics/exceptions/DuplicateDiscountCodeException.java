package datn.com.cosmetics.exceptions;

public class DuplicateDiscountCodeException extends RuntimeException {
    public DuplicateDiscountCodeException(String message) {
        super(message);
    }
}
