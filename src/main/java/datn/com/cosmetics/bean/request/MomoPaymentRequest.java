
package datn.com.cosmetics.bean.request;

import lombok.Data;

@Data
public class MomoPaymentRequest {
    String orderId;
    double total;

}