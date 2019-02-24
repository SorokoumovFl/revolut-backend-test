package revolut.banking.exception.banking;

public class NoSuchAccountException extends BankingException {

    @Override
    public String getMessage() {
        return "No such account";
    }

}
