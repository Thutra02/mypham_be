package datn.com.cosmetics.exceptions;

public class DuplicateDiscountNameException extends RuntimeException {
    public DuplicateDiscountNameException(String message) {
        super(message);
    }
}
