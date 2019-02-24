package revolut.banking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.util.PathTemplateMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revolut.banking.request.ChangeAccountRequest;
import revolut.banking.request.CreateAccountRequest;
import revolut.banking.request.TransferRequest;
import revolut.banking.response.BankingExceptionMapper;
import revolut.banking.response.BaseResponse;
import revolut.banking.exception.banking.BankingException;
import revolut.banking.model.AccountId;
import revolut.banking.persistence.command.AccountEventStoreRepository;
import revolut.banking.persistence.command.TransferEventStoreRepository;
import revolut.banking.persistence.query.ReadRepository;
import revolut.banking.projection.AccountProjection;
import revolut.banking.service.command.BankingCommandService;
import revolut.banking.service.query.BankingQueryService;
import revolut.banking.service.query.BankingQueryServiceImpl;

import java.io.IOException;

public class ApiController {

    private static final AccountEventStoreRepository accountRepository = new AccountEventStoreRepository();
    private static final TransferEventStoreRepository transferRepository = new TransferEventStoreRepository();
    private static final ReadRepository readRepository = new ReadRepository();

    private static final BankingCommandService command = BankingCommandService.create(accountRepository, transferRepository);
    private static final BankingQueryService query = new BankingQueryServiceImpl(readRepository);

    private static final ObjectMapper mapper = ObjectMapperFactory.get();

    private ApiController() {
        this.accountRepository.addListener(readRepository);
        this.transferRepository.addListener(readRepository);
    }

    public static ApiController create() {
        return new ApiController();
    }

    public void getAccount(HttpServerExchange exchange) throws BankingException, IOException {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        String accountId = pathMatch.getParameters().get("accountId");
        AccountProjection projection = query.getAccount(AccountId.from(accountId));
        sendResponse(exchange, BaseResponse.ok(projection));
    }

    public void createAccount(HttpServerExchange exchange) throws IOException {
        CreateAccountRequest request = getBody(exchange, CreateAccountRequest.class);
        AccountId newAccountId = command.createAccount(request.getOwnerName());
        sendResponse(exchange, BaseResponse.ok(newAccountId.toString()));
    }

    public void changeAccount(HttpServerExchange exchange) throws BankingException, IOException {
        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        ChangeAccountRequest request = getBody(exchange, ChangeAccountRequest.class);
        AccountId id = AccountId.from(pathMatch.getParameters().get("accountId"));
        command.changeAccount(id, request.getOwnerName());
        sendResponse(exchange, BaseResponse.ok());
    }

    public void transfer(HttpServerExchange exchange) throws BankingException, IOException {
        TransferRequest request = getBody(exchange, TransferRequest.class);
        command.transfer(request.getAmount(), request.getPayerId(), request.getBeneficiaryId());
        sendResponse(exchange, BaseResponse.ok());
    }

    public void transferHistory(HttpServerExchange exchange) throws IOException {
        sendResponse(exchange, BaseResponse.ok(query.getTotalTransfersHistory()));
    }

    public void bankingExceptionHandler(HttpServerExchange exchange) throws IOException {
        BankingException reason = (BankingException)exchange.getAttachment(ExceptionHandler.THROWABLE);
        sendResponse(exchange, BankingExceptionMapper.map(reason));
    }

    public void exceptionHandler(HttpServerExchange exchange) {
        exchange.setStatusCode(500);
    }

    private void sendResponse(HttpServerExchange exchange, BaseResponse response) throws JsonProcessingException {
        exchange.setStatusCode(response.getHttpCode());
        exchange.getResponseSender().send(mapper.writeValueAsString(response));
    }

    private <T> T getBody(final HttpServerExchange exchange, Class<T> clazz) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        exchange.getRequestReceiver().receiveFullString((ex, data) -> requestBody.append(data));
        return mapper.readValue(requestBody.toString(), clazz);
    }

}
