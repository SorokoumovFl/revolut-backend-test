package revolut.banking;

import io.undertow.Handlers;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.banking.exception.banking.BankingException;

public class BankingServer {

    private final BankingServiceConfig config;

    private static final Logger logger = LoggerFactory.getLogger("Banking Web Service");

    public BankingServer(BankingServiceConfig config) {
        this.config = config;
    }

    public void start() {
        try {
            ApiController controller = ApiController.create();
            Undertow server = Undertow.builder()
                    .addHttpListener(config.getHttpPort(), config.getHttpHost())
                    .setHandler(Handlers.exceptionHandler(Handlers.path()
                            .addPrefixPath("/account", Handlers.routing()
                                    .post("", controller::createAccount)
                                    .put("/{accountId}", controller::changeAccount)
                                    .get("/{accountId}", controller::getAccount)
                            )
                            .addPrefixPath("/transfer", Handlers.routing()
                                    .post("", controller::transfer)
                                    .get("/history", controller::transferHistory)
                            ))

                            .addExceptionHandler(BankingException.class, controller::bankingExceptionHandler)
                            .addExceptionHandler(Throwable.class, controller::exceptionHandler))
                    .build();
            server.start();
        } catch (Throwable t) {
            logger.error("Unable to start banking web service", t);
            throw t;
        }
    }
}
