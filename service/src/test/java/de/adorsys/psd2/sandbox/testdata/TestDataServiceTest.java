package de.adorsys.psd2.sandbox.testdata;

import de.adorsys.psd2.sandbox.testdata.domain.Account;
import de.adorsys.psd2.sandbox.testdata.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDataServiceTest {

  private static final String ACCOUNT_ID_GIRO = "9b86539d-589b-4082-90c2-d725c019777f";
  private static final String IBAN_GIRO = "DE94500105178833114935";

  private static final String ACCOUNT_ID_SAVINGS = "d460057b-053a-490a-a36e-c0c8afb735e9";
  private static final String IBAN_SAVINGS = "DE96500105179669622432";

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
    Assert.assertTrue(transactionList.contains(expectedTransaction));
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
    Assert.assertTrue(accounts.contains(giroAccount));
  }

  @Test
  public void getNumberOfTransactionsPerAccountSuccessful() {
    List<Transaction> giroTransactionList = testDataService.getTransactions(ACCOUNT_ID_GIRO);
    List<Transaction> savingsTransactionList = testDataService.getTransactions(ACCOUNT_ID_SAVINGS);

    Assert.assertEquals(giroTransactionList.size(), 5);
    Assert.assertEquals(savingsTransactionList.size(), 5);
  }

  @Test
  public void getNumberOfAccountsSuccessful() {
    List<Account> accounts = testDataService.getAccounts();
    Assert.assertEquals(accounts.size(), 2);
  }

  @Test
  public void getAccountDetailsSuccessful() {
    Account giroAccount = testDataService.getAccountDetails(ACCOUNT_ID_GIRO);

    Assert.assertEquals(giroAccount.getAmount(), BigDecimal.valueOf(1500));
    Assert.assertEquals(giroAccount.getCurrency(), Currency.getInstance("EUR"));
    Assert.assertEquals(giroAccount.getIban(), IBAN_GIRO);

    Account savingsAccount = testDataService.getAccountDetails(ACCOUNT_ID_SAVINGS);

    Assert.assertEquals(savingsAccount.getAmount(), BigDecimal.valueOf(2300));
    Assert.assertEquals(savingsAccount.getCurrency(), Currency.getInstance("EUR"));
    Assert.assertEquals(savingsAccount.getIban(), IBAN_SAVINGS);
  }

  @Test
  public void getFirstTransactionDetailsSuccessful() {
    Transaction transaction = testDataService
        .getTransactionDetails(ACCOUNT_ID_GIRO, "b2789674-1ea8-4a0d-a9e3-01319bd72d2e");

    Assert.assertEquals(transaction.getAmount(), BigDecimal.valueOf(-50));
    Assert.assertEquals(transaction.getBookingDate(), LocalDate.parse("2018-07-22"));
    Assert.assertEquals(transaction.getCurrency(), Currency.getInstance("EUR"));
    Assert.assertEquals(transaction.getCreditorAccount(), "DE48500105171923711479");
    Assert.assertEquals(transaction.getRemittanceInfo(), "Spende Greenpeace");
    Assert.assertEquals(transaction.getCreditorName(), "Greenpeace");
    Assert.assertEquals(transaction.getDebtorAccount(), IBAN_GIRO);
    Assert.assertEquals(transaction.getDebtorName(), "Melanie Klein");
  }
}
