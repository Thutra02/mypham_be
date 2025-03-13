package datn.com.cosmetics.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MomoResponse {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("resultCode")
    private int resultCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("transId")
    private Long transId;

    @JsonProperty("payType")
    private String payType;

    @JsonProperty("responseTime")
    private Long responseTime;

    @JsonProperty("extraData")
    private String extraData;
}
