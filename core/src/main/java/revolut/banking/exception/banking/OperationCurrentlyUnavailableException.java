package revolut.banking.exception.banking;

public class OperationCurrentlyUnavailableException extends BankingException {

    @Override
    public String getMessage() {
        return "Operation currently unavailable, please try again";
    }

}
