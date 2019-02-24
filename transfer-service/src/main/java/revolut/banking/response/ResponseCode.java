package revolut.banking.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseCode {
    OK(0),
    NO_SUCH_ACCOUNT(1),
    INSUFFICIENT_FUNDS(2),
    OPERATION_CURRENTLY_UNAVAILABLE(3),

    UNKNOWN_ERROR(-1)
    ;

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}
