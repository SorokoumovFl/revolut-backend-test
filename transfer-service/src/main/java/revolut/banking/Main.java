package revolut.banking;

public class Main {

    public static void main(String[] args) {
        try {
            BankingServiceConfig config = BankingServiceConfig.load("web");
            BankingServer server = new BankingServer(config);
            server.start();
        } catch (Throwable t) {
            System.exit(1);
        }
    }

}
