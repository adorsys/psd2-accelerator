package de.adorsys.psd2.sandbox.portal.testdata;

import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.portal.testdata.domain.TestPsu;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Transaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiBalanceType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataMapper {

  private final TestDataService testDataService;

  @Autowired
  public TestDataMapper(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  /**
   * Maps TestData Account to SpiAccount.
   *
   * @param account TestData Account
   * @return SpiAccount
   */
  public SpiAccountDetails mapAccountToSpiAccount(Account account) {
    return new SpiAccountDetails(
        account.getAccountId(),
        account.getAccountId(),
        account.getIban(),
        "",
        "",
        "",
        "",
        account.getCurrency(),
        "",
        account.getProduct(),
        account.getCashAccountType() != null
            ? SpiAccountType.getByValue(account.getCashAccountType().value()).get() : null,
        null,
        "",
        "",
        null,
        "",
        account.getBalance() != null
            ? new ArrayList<>(Arrays.asList(mapBalanceToSpiBalance(account.getBalance())))
            : null);
  }

  /**
   * Maps TestData Transaction to SpiTransaction.
   *
   * @param transaction TestData Transaction
   * @return SpiTransaction
   */
  public SpiTransaction mapTransactionToSpiTransaction(
      Transaction transaction) {
    return new SpiTransaction(
        transaction.getTransactionId(),
        null,
        null,
        null,
        null,
        null,
        transaction.getBookingDate(),
        null,
        new SpiAmount(transaction.getCurrency(), transaction.getAmount()),
        null,
        transaction.getCreditorName(),
        new SpiAccountReference(
            this.mapAccountToSpiAccount(
                this.getAccountData(transaction.getCreditorAccount()))),
        null,
        null,
        new SpiAccountReference(
            this.mapAccountToSpiAccount(getAccountData(transaction.getDebtorAccount()))),
        transaction.getDebtorName(),
        transaction.getRemittanceInfo(),
        transaction.getRemittanceInfo(),
        null,
        null,
        null
    );
  }

  /**
   * Maps TestData Balance to SpiAccountBalance.
   *
   * @param balance TestData Balance
   * @return SpiAccountBalance
   */
  public SpiAccountBalance mapBalanceToSpiBalance(Balance balance) {
    SpiAccountBalance spiAccountBalance = new SpiAccountBalance();

    SpiAmount spiAmount = new SpiAmount(balance.getBalanceAmount().getCurrency(),
        balance.getBalanceAmount().getAmount());
    spiAccountBalance.setSpiBalanceAmount(spiAmount);
    spiAccountBalance.setSpiBalanceType(SpiBalanceType.AVAILABLE);

    return spiAccountBalance;
  }

  private Account getAccountData(String iban) {
    Optional<TestPsu> psuId = this.testDataService.getPsuByIban(iban);

    if (!psuId.isPresent()) {
      return new Account(iban, Currency.getInstance("EUR"));
    }

    Optional<Account> account = testDataService.getDistinctAccount(psuId.get().getPsuId(), iban);

    return account.orElseGet(() -> new Account(iban, Currency.getInstance("EUR")));
  }
}
