package revolut.banking.service.command;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import revolut.banking.exception.banking.BankingException;
import revolut.banking.exception.banking.InsufficientFundsException;
import revolut.banking.exception.banking.NoSuchAccountException;
import revolut.banking.exception.banking.OperationCurrentlyUnavailableException;
import revolut.banking.model.AccountId;
import revolut.banking.persistence.command.AccountEventStoreRepository;
import revolut.banking.persistence.command.TransferEventStoreRepository;
import revolut.banking.persistence.query.ReadRepository;
import revolut.banking.projection.AccountProjection;
import revolut.banking.service.query.BankingQueryService;
import revolut.banking.service.query.BankingQueryServiceImpl;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.testng.Assert.*;

public class BankingServicesTest {

    private static final long MINOR_DELAY = 200;
    private static final long OPTIMISTIC_LOCKING_TEST_TASKS = 10_000;

    private AccountEventStoreRepository account;
    private TransferEventStoreRepository transfer;
    private BankingCommandService command;
    private BankingQueryService query;
    private ReadRepository read;

    private AccountId firstAccount;
    private AccountId secondAccount;

    @BeforeClass
    public void setUp() {
        account = new AccountEventStoreRepository();
        transfer = new TransferEventStoreRepository();
        command = new BankingCommandServiceImpl(account, transfer);
        read = new ReadRepository();
        query = new BankingQueryServiceImpl(read);

        account.addListener(read);
        transfer.addListener(read);
    }

    @Test(expectedExceptions = NoSuchAccountException.class)
    public void nonexistentAccountRetrievalTest() throws BankingException {
        query.getAccount(AccountId.generate());
    }

    @Test()
    public void createAndChangeAccountTest() throws BankingException, InterruptedException {
        String ownerName = "John Doe";
        String newOwnerName = "Hong Gildong";

        firstAccount = command.createAccount(ownerName);

        // A minor delay is required because of the eventual consistency
        Thread.sleep(MINOR_DELAY);
        AccountProjection projection = query.getAccount(firstAccount);
        assertEquals(projection.getAccountOwner(), ownerName);

        command.changeAccount(firstAccount, newOwnerName);
        Thread.sleep(MINOR_DELAY);
        assertEquals(projection.getAccountOwner(), newOwnerName);
    }

    @Test(priority = 1)
    public void refillTest() throws BankingException, InterruptedException {
        secondAccount = command.createAccount("Rudolf Lingens");

        command.transfer(new BigDecimal(64), null, secondAccount);
        Thread.sleep(MINOR_DELAY);
        assertEquals(query.getAccount(secondAccount).getBalance(), new BigDecimal(64));

        command.transfer(new BigDecimal(128), null, secondAccount);
        Thread.sleep(MINOR_DELAY);
        assertEquals(query.getAccount(secondAccount).getBalance(), new BigDecimal(192));
    }

    @Test(priority = 2)
    public void transferTest() throws BankingException, InterruptedException {
        command.transfer(new BigDecimal(64), secondAccount, firstAccount);
        Thread.sleep(MINOR_DELAY);
        assertEquals(query.getAccount(secondAccount).getBalance(), new BigDecimal(128));
        assertEquals(query.getAccount(firstAccount).getBalance(), new BigDecimal(64));

        // Two transfers from previous tests + current transfer
        assertEquals(query.getTotalTransfersHistory().size(), 3);
        assertEquals(query.getTotalTransfersHistory().iterator().next().getAmount(), new BigDecimal(64));
    }

    @Test(expectedExceptions = InsufficientFundsException.class, priority = 3)
    public void insufficientFundsTest() throws BankingException {
        command.transfer(new BigDecimal(9001), secondAccount, firstAccount);
    }

    @Test(expectedExceptions = NoSuchAccountException.class, priority = 4)
    public void transferToNowhereTest() throws BankingException {
        command.transfer(BigDecimal.ONE, secondAccount, null);
    }

    @Test(expectedExceptions = NoSuchAccountException.class, priority = 4)
    public void transferFromNonexistentAccount() throws BankingException {
        command.transfer(BigDecimal.ONE, AccountId.generate(), firstAccount);
    }

    @Test(expectedExceptions = NoSuchAccountException.class, priority = 4)
    public void transferToNonexistentAccount() throws BankingException {
        command.transfer(BigDecimal.ONE, secondAccount, AccountId.generate());
    }

    /**
     * Transfer service optimistic locking PoC
     */
    @Test(priority = 5)
    public void optimisticLockingTest() throws BankingException, InterruptedException {
        AtomicBoolean terminated = new AtomicBoolean(false);
        command.transfer(new BigDecimal(Integer.MAX_VALUE), null, secondAccount);

        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 0; i < OPTIMISTIC_LOCKING_TEST_TASKS; i++) {
            service.submit(() -> {
                try {
                    command.transfer(BigDecimal.ONE, secondAccount, firstAccount);
                } catch (OperationCurrentlyUnavailableException e) {
                    terminated.set(true);
                } catch (Throwable ignore) {
                }
            });
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
        assertTrue(terminated.get());
    }
}
