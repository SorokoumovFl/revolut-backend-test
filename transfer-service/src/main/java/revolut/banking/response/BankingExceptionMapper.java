package revolut.banking.response;

import revolut.banking.exception.banking.BankingException;
import revolut.banking.exception.banking.InsufficientFundsException;
import revolut.banking.exception.banking.NoSuchAccountException;
import revolut.banking.exception.banking.OperationCurrentlyUnavailableException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static revolut.banking.response.ResponseCode.*;

public class BankingExceptionMapper {

    private static final Map<Class<? extends BankingException>, Function> map = new HashMap<>();

    static {
        addException(InsufficientFundsException.class, e -> BaseResponse.response(
                INSUFFICIENT_FUNDS,
                "Insufficient funds on the account " + e.getAccountId().toString() + ", required " + e.getAmount(),
                400)
        );

        addException(OperationCurrentlyUnavailableException.class, exception ->
                BaseResponse.response(OPERATION_CURRENTLY_UNAVAILABLE, exception.getMessage(), 500));

        addException(NoSuchAccountException.class, exception -> BaseResponse.response(
                NO_SUCH_ACCOUNT,
                exception.getMessage(),
                404)
        );
    }

    public static <E extends BankingException> void addException(Class<E> exception, Function<E, BaseResponse> handler) {
        map.put(exception, handler);
    }

    public static <E extends BankingException> BaseResponse map(E exception) {
        return (BaseResponse) map.getOrDefault(exception.getClass(), e -> BaseResponse.UNKNOWN_ERROR).apply(exception);
    }


}
