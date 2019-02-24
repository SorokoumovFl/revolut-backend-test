package revolut.banking.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangeAccountRequest {

    private final String ownerName;

    public ChangeAccountRequest(@JsonProperty("owner-name") String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
