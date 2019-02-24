package revolut.banking.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseResponse<T> {

    public static final BaseResponse UNKNOWN_ERROR =
            new BaseResponse(ResponseCode.UNKNOWN_ERROR, "Unknown error", null, 500);
    private static final Integer HTTP_OK = 200;

    private static final String OK = "ok";

    private final ResponseCode code;

    private final String message;

    private final T payload;

    @JsonIgnore
    private Integer httpCode;

    @JsonCreator
    private BaseResponse(
            @JsonProperty("code") ResponseCode code,
            @JsonProperty("message") String message,
            @JsonProperty("payload") T payload,
            @JsonProperty("httpCode") Integer httpCode) {
        this.code = code;
        this.message = message;
        this.payload = payload;
        this.httpCode = httpCode;
    }

    public static BaseResponse response(ResponseCode status, String message, Integer httpCode) {
        return new BaseResponse(status, message, null, httpCode);
    }

    public static <T> BaseResponse ok(T payload) {
        return new BaseResponse(ResponseCode.OK, OK, payload, HTTP_OK);
    }

    public static BaseResponse ok() {
        return new BaseResponse(ResponseCode.OK, OK, null, HTTP_OK);
    }

    public ResponseCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getPayload() {
        return payload;
    }

    public Integer getHttpCode() {
        return httpCode;
    }
}
