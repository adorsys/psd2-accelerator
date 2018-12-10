package de.adorsys.psd2.sandbox.portal.testdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TestDataServiceTest {

  private static final String ACCOUNT_ID_GIRO = TestDataService.ACCOUNT_ID_GIRO;
  private static final String IBAN_GIRO = TestDataService.IBAN_GIRO;
  private static final String ACCOUNT_ID_SAVINGS = TestDataService.ACCOUNT_ID_SAVINGS;
  private static final String IBAN_SAVINGS = TestDataService.IBAN_SAVINGS;

  private TestDataService testDataService;

  @Before
  public void initService() {
    testDataService = new TestDataService();
  }

  @Test
  public void getTransactionFromTransactionsSuccessful() {
    Transaction expectedTransaction = new Transaction(
        "b2789674-1ea8-4a0d-a9e3-01319bd72d2e",
        BigDecimal.valueOf(-50),
        Currency.getInstance("EUR"),
        LocalDate.parse("2018-07-22"),
        "Melanie Klein",
        "DE94500105178833114935",
        "Greenpeace",
        "DE48500105171923711479",
        "Spende Greenpeace"
    );
    List<Transaction> transactionList = testDataService.getTransactions(ACCOUNT_ID_GIRO);
    assertTrue(transactionList.contains(expectedTransaction));
  }

  @Test
  public void getAccountFromAccountListSuccessful() {
    Account giroAccount = new Account(
        "9b86539d-589b-4082-90c2-d725c019777f",
        "DE94500105178833114935",
        Currency.getInstance("EUR"),
        BigDecimal.valueOf(1500)
    );
    List<Account> accounts = testDataService.getAccounts();
    assertTrue(accounts.contains(giroAccount));
  }

  @Test
  public void getNumberOfTransactionsPerAccountSuccessful() {
    List<Transaction> giroTransactionList = testDataService.getTransactions(ACCOUNT_ID_GIRO);
    List<Transaction> savingsTransactionList = testDataService.getTransactions(ACCOUNT_ID_SAVINGS);

    assertEquals(giroTransactionList.size(), 5);
    assertEquals(savingsTransactionList.size(), 5);
  }

  @Test
  public void getNumberOfAccountsSuccessful() {
    List<Account> accounts = testDataService.getAccounts();
    assertEquals(accounts.size(), 2);
  }

  @Test
  public void getAccountDetailsSuccessful() {
    Account giroAccount = testDataService.getAccountDetails(ACCOUNT_ID_GIRO);

    assertEquals(giroAccount.getAmount(), BigDecimal.valueOf(1500));
    assertEquals(giroAccount.getCurrency(), Currency.getInstance("EUR"));
    assertEquals(giroAccount.getIban(), IBAN_GIRO);

    Account savingsAccount = testDataService.getAccountDetails(ACCOUNT_ID_SAVINGS);

    assertEquals(savingsAccount.getAmount(), BigDecimal.valueOf(2300));
    assertEquals(savingsAccount.getCurrency(), Currency.getInstance("EUR"));
    assertEquals(savingsAccount.getIban(), IBAN_SAVINGS);
  }

  @Test
  public void getFirstTransactionDetailsSuccessful() {
    Transaction transaction = testDataService
        .getTransactionDetails(ACCOUNT_ID_GIRO, "b2789674-1ea8-4a0d-a9e3-01319bd72d2e");

    assertEquals(transaction.getAmount(), BigDecimal.valueOf(-50));
    assertEquals(transaction.getBookingDate(), LocalDate.parse("2018-07-22"));
    assertEquals(transaction.getCurrency(), Currency.getInstance("EUR"));
    assertEquals(transaction.getCreditorAccount(), "DE48500105171923711479");
    assertEquals(transaction.getRemittanceInfo(), "Spende Greenpeace");
    assertEquals(transaction.getCreditorName(), "Greenpeace");
    assertEquals(transaction.getDebtorAccount(), IBAN_GIRO);
    assertEquals(transaction.getDebtorName(), "Melanie Klein");
  }
}
