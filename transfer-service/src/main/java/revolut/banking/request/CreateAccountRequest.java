package revolut.banking.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAccountRequest {

    private final String ownerName;

    public CreateAccountRequest(@JsonProperty("owner-name") String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
