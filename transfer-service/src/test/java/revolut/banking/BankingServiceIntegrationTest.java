package revolut.banking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import revolut.banking.model.AccountId;
import revolut.banking.projection.AccountProjection;
import revolut.banking.projection.TransferProjection;
import revolut.banking.request.CreateAccountRequest;
import revolut.banking.request.TransferRequest;
import revolut.banking.response.BaseResponse;
import revolut.banking.response.ResponseCode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class BankingServiceIntegrationTest {

    private static final long MINOR_DELAY = 200;
    private static final String FIRST_OWNER = "John Doe";
    private static final String SECOND_OWNER = "Hong Gildong";

    private static final TypeReference ACCOUNT_TYPE_REFERENCE =
            new TypeReference<BaseResponse<AccountProjection>>() {};
    private static final TypeReference TRANSFER_HISTORY_TYPE_REFERENCE =
            new TypeReference<BaseResponse<List<TransferProjection>>>() {};

    private BankingServiceConfig config;
    private BankingServer server;
    private ObjectMapper mapper;
    
    private String baseUrl;

    private AccountId firstId;
    private AccountId secondId;

    @BeforeClass
    public void setUp() {
        this.config = BankingServiceConfig.load("web");
        this.server = new BankingServer(config);
        this.baseUrl = "http://" + config.getHttpHost() + ":" + config.getHttpPort();
        this.server.start();
        this.mapper = ObjectMapperFactory.get();
    }

    @Test
    public void getNonexistentAccountTest() throws UnirestException, IOException {
        HttpResponse<String> srcResponse = Unirest
                .get(baseUrl + "/account/some_nonexistent_id")
                .asString();

        BaseResponse response = mapper.readValue(srcResponse.getBody(), BaseResponse.class);
        assertEquals(srcResponse.getStatus(), 404);
        assertEquals(response.getCode(), ResponseCode.NO_SUCH_ACCOUNT);
    }

    @Test
    public void createAccountTest() throws UnirestException, IOException {
        firstId = createAccount(FIRST_OWNER);
    }

    @Test(priority = 1)
    public void checkExistingAccountTest() throws UnirestException, IOException, InterruptedException {
        Thread.sleep(MINOR_DELAY);
        HttpResponse<String> srcResponse = Unirest.get(baseUrl + "/account/" + firstId).asString();
        assertEquals(srcResponse.getStatus(), 200);

        BaseResponse<AccountProjection> response = mapper.readValue(srcResponse.getBody(), ACCOUNT_TYPE_REFERENCE);
        assertEquals(response.getPayload().getAccountOwner(), FIRST_OWNER);
        assertEquals(response.getPayload().getBalance(), BigDecimal.ZERO);
    }

    @Test(priority = 2)
    public void refillExistingAccountTest() throws IOException, UnirestException, InterruptedException {
        TransferRequest transferRequest = new TransferRequest(null, firstId, new BigDecimal(150));
        HttpResponse<String> srcResponse = Unirest
                .post(baseUrl + "/transfer/")
                .body(mapper.writeValueAsString(transferRequest))
                .asString();
        assertEquals(srcResponse.getStatus(), 200);

        Thread.sleep(MINOR_DELAY);

        AccountProjection account = getAccount(firstId);
        assertEquals(account.getBalance(), new BigDecimal(150));
    }

    @Test(priority = 3)
    public void transferTest() throws IOException, UnirestException, InterruptedException {
        secondId = createAccount(SECOND_OWNER);

        Thread.sleep(MINOR_DELAY);

        TransferRequest transferRequest = new TransferRequest(firstId, secondId, new BigDecimal(50));
        HttpResponse<String> srcResponse = Unirest
                .post(baseUrl + "/transfer/")
                .body(mapper.writeValueAsString(transferRequest))
                .asString();
        assertEquals(srcResponse.getStatus(), 200);
        assertEquals(mapper.readValue(srcResponse.getBody(), BaseResponse.class).getCode(), ResponseCode.OK);

        Thread.sleep(MINOR_DELAY);

        assertEquals(getAccount(firstId).getBalance(), new BigDecimal(100));
        assertEquals(getAccount(secondId).getBalance(), new BigDecimal(50));
    }

    @Test(priority = 4)
    public void insufficientFundsTest() throws IOException, UnirestException {
        TransferRequest transferRequest = new TransferRequest(firstId, secondId, new BigDecimal(9001));
        HttpResponse<String> srcResponse = Unirest
                .post(baseUrl + "/transfer/")
                .body(mapper.writeValueAsString(transferRequest))
                .asString();
        assertEquals(srcResponse.getStatus(), 400);
        assertEquals(mapper.readValue(srcResponse.getBody(), BaseResponse.class).getCode(), ResponseCode.INSUFFICIENT_FUNDS);

        assertEquals(getAccount(firstId).getBalance(), new BigDecimal(100));
        assertEquals(getAccount(secondId).getBalance(), new BigDecimal(50));
    }

    // There must be 2 transfers by now
    @Test(priority = 5)
    public void transferHistoryTest() throws IOException, UnirestException {
        HttpResponse<String> srcResponse = Unirest
                .get(baseUrl + "/transfer/history")
                .asString();
        assertEquals(srcResponse.getStatus(), 200);

        BaseResponse<List<TransferProjection>> transfers = mapper.readValue(srcResponse.getBody(), TRANSFER_HISTORY_TYPE_REFERENCE);
        assertEquals(transfers.getCode(), ResponseCode.OK);
        assertEquals(transfers.getPayload().size(), 2);
    }

    private AccountId createAccount(String ownerName) throws IOException, UnirestException {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest(ownerName);
        HttpResponse<String> srcResponse = Unirest
                .post(baseUrl + "/account/")
                .body(mapper.writeValueAsString(createAccountRequest))
                .asString();

        assertEquals(srcResponse.getStatus(), 200);
        BaseResponse<String> response = mapper.readValue(srcResponse.getBody(), new TypeReference<BaseResponse<String>>() {});
        assertEquals(response.getCode(), ResponseCode.OK);
        return AccountId.from(response.getPayload());
    }

    private AccountProjection getAccount(AccountId id) throws UnirestException, IOException {
        HttpResponse<String> srcResponse = Unirest.get(baseUrl + "/account/" + id.toString()).asString();
        assertEquals(srcResponse.getStatus(), 200);

        BaseResponse<AccountProjection> response = mapper.readValue(srcResponse.getBody(), ACCOUNT_TYPE_REFERENCE);
        assertEquals(response.getCode(), ResponseCode.OK);
        return response.getPayload();
    }

}
