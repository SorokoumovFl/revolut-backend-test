package revolut.banking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class BankingServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger("WebConfig loader");

    private ResourceBundle bundle;

    private String httpHost = "localhost";
    private Integer httpPort = 8080;

    public static BankingServiceConfig load(String resourceFile) {
        return new BankingServiceConfig(resourceFile);
    }

    private BankingServiceConfig(String resourceFile) {
        try {
            this.bundle = ResourceBundle.getBundle(resourceFile);
            loadProperty("http.host", host -> httpHost = host);
            loadProperty("http.port", port -> {
                try {
                    httpPort = Integer.valueOf(port);
                } catch (NumberFormatException ignore) {
                }
            });
        } catch (MissingResourceException e) {
            logger.info("No {} resource file found, using default config values", resourceFile);
        }
    }

    public String getHttpHost() {
        return httpHost;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    private void loadProperty(String key, Consumer<String> consumer) {
        try {
            String value = bundle.getString(key);
            consumer.accept(value);
        } catch (MissingResourceException | ClassCastException ignore) {
        }
    }
}
