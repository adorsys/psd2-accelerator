package de.adorsys.psd2.sandbox.portal.testdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.PsuData;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Transaction;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

public class TestDataServiceTest {

  private TestDataService testDataService;

  public TestDataServiceTest() {
    this.testDataService = new TestDataService();
  }

  @Before
  public void initService() {
    testDataService = new TestDataService();
  }

  @Test
  public void getPsuTestSuccessful() {
    final String psuId = "PSU-1";
    Optional<PsuData> optionalPsu = testDataService.getPsu(psuId);

    assertEquals(optionalPsu.get().getPsuId(), psuId);
  }

  @Test
  public void getPsuTestWithErrors() {
    final String psuId = "PSU-UNKNOWN";
    Optional<PsuData> optionalPsu = testDataService.getPsu(psuId);

    assertEquals(optionalPsu, Optional.empty());
  }

  @Test
  public void getPsuIdByIbanTestSuccessful() {
    final String expectedPsuId = "PSU-1";
    Optional<String> returnedPsuId1 = testDataService.getPsuByIban("DE94500105178833114935");
    Optional<String> returnedPsuId2 = testDataService.getPsuByIban("DE96500105179669622432");

    assertEquals(returnedPsuId1.get(), expectedPsuId);
    assertEquals(returnedPsuId2.get(), expectedPsuId);
  }

  @Test
  public void getPsuIdByIbanTestWithErrors() {
    Optional<String> returnedPsuId = testDataService.getPsuByIban("DE94500105178833114936");

    assertEquals(returnedPsuId, Optional.empty());
  }

  @Test
  public void getRequestedAccountsTestSuccessful() {
    final String psuId = "PSU-1";
    final String iban1 = "DE94500105178833114935";
    final String iban2 = "DE96500105179669622432";
    final List<String> ibans = Arrays.asList(iban1, iban2);

    Optional<List<Account>> accounts = testDataService.getRequestedAccounts(psuId, ibans);

    if (!accounts.isPresent()) {
      fail();
    }

    Account account1 = accounts.get().stream()
        .filter(account -> account.getIban().equals(iban1))
        .collect(Collectors.toList()).get(0);

    Account account2 = accounts.get().stream()
        .filter(account -> account.getIban().equals(iban2))
        .collect(Collectors.toList()).get(0);

    assertEquals(accounts.get().size(), ibans.size());
    assertTrue(ibans.contains(account1.getIban()));
    assertTrue(ibans.contains(account2.getIban()));
  }

  @Test
  public void getSingleRequestedAccountsTestSuccessful() {
    final String psuId = "PSU-1";
    final String iban = "DE96500105179669622432";
    final List<String> ibans = Arrays.asList(iban);

    Optional<List<Account>> accounts = testDataService.getRequestedAccounts(psuId, ibans);

    if (!accounts.isPresent()) {
      fail();
    }

    Account requestedAccount = accounts.get().stream()
        .filter(account -> account.getIban().equals(iban))
        .collect(Collectors.toList()).get(0);

    assertEquals(accounts.get().size(), ibans.size());
    assertTrue(ibans.contains(requestedAccount.getIban()));
  }

  @Test
  public void getRequestedAccountsTestWithUnkonwnPsu() {
    final String psuId = "PSU-UNNKOWN";
    final String accountIdGrio = "9b86539d-589b-4082-90c2-d725c019777f";

    Optional<Account> account = testDataService.getDistinctAccount(psuId, accountIdGrio);

    assertEquals(account, Optional.empty());
  }

  @Test
  public void getDistinctAccountTestSuccessful() {
    final String psuId = "PSU-1";
    final String accountIdGrio = "9b86539d-589b-4082-90c2-d725c019777f";

    Optional<Account> account = testDataService.getDistinctAccount(psuId, accountIdGrio);

    if (!account.isPresent()) {
      fail();
    }

    assertEquals(account.get().getAccountId(), accountIdGrio);
  }

  @Test
  public void getDistinctAccountTestWithUnkonwnPsu() {
    final String psuId = "PSU-UNNKOWN";
    final String accountIdGrio = "9b86539d-589b-4082-90c2-d725c019777f";

    Optional<Account> account = testDataService.getDistinctAccount(psuId, accountIdGrio);

    assertEquals(account, Optional.empty());
  }

  @Test
  public void getAccountsTestSuccessful() {
    final String psuId = "PSU-1";

    Optional<List<Account>> accounts = testDataService.getAccounts(psuId);

    if (!accounts.isPresent()) {
      fail();
    }

    assertEquals(accounts.get().size(), 2);
    assertNotNull(accounts.get().get(0));
    assertNotNull(accounts.get().get(1));
  }


  @Test
  public void getAccountsTestWithUnkonwnPsu() {
    final String psuId = "PSU-UNNKOWN";

    Optional<List<Account>> account = testDataService.getAccounts(psuId);

    assertEquals(account, Optional.empty());
  }

  @Test
  public void getTransactionsTestSuccessful() {
    final String psuId = "PSU-1";
    final String accountIdGrio = "9b86539d-589b-4082-90c2-d725c019777f";
    final String accountIdSavings = "d460057b-053a-490a-a36e-c0c8afb735e9";

    Optional<List<Transaction>> giroTransactions = testDataService
        .getTransactions(psuId, accountIdGrio);
    Optional<List<Transaction>> accountTransactions = testDataService
        .getTransactions(psuId, accountIdSavings);

    if (!giroTransactions.isPresent() || !accountTransactions.isPresent()) {
      fail();
    }

    assertEquals(giroTransactions.get().size(), 5);
    assertEquals(accountTransactions.get().size(), 5);
    for (Transaction transaction : giroTransactions.get()) {
      assertNotNull(transaction);
    }
    for (Transaction transaction : accountTransactions.get()) {
      assertNotNull(transaction);
    }
  }

  @Test
  public void getTransactionsTestWithUnkonwnPsu() {
    final String psuId = "PSU-UNNKOWN";
    final String accountId = "9b86539d-589b-4082-90c2-d725c019777f";

    Optional<List<Transaction>> account = testDataService.getTransactions(psuId, accountId);

    assertEquals(account, Optional.empty());
  }

  @Test
  public void getDistinctTransactionTestSuccessful() {
    final String psuId = "PSU-1";
    final String accountIdSavings = "d460057b-053a-490a-a36e-c0c8afb735e9";
    final String transactionId = "8508921e-2cd4-43e8-ba1e-26b143307927";

    Optional<Transaction> returnedTransaction = testDataService
        .getDistinctTransaction(psuId, accountIdSavings, transactionId);

    if (!returnedTransaction.isPresent()) {
      fail();
    }

    assertEquals(returnedTransaction.get().getTransactionId(), transactionId);
  }

  @Test
  public void getDistinctTransactionTestWithUnkonwnPsu() {
    final String psuId = "PSU-UNNKOWN";
    final String accountId = "9b86539d-589b-4082-90c2-d725c019777f";
    final String transactionId = "8508921e-2cd4-43e8-ba1e-26b143307927";

    Optional<Transaction> account = testDataService.getDistinctTransaction(psuId, accountId, transactionId);

    assertEquals(account, Optional.empty());
  }

  @Test
  public void getDistinctTransactionTestWithUnkonwnAccount() {
    final String psuId = "PSU-1";
    final String accountId = "ACCOUNT_UNKOWN";
    final String transactionId = "8508921e-2cd4-43e8-ba1e-26b143307927";

    Optional<Transaction> account = testDataService.getDistinctTransaction(psuId, accountId, transactionId);

    assertEquals(account, Optional.empty());
  }
}
