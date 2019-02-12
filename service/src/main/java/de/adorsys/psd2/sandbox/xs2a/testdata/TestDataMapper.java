package de.adorsys.psd2.sandbox.xs2a.testdata;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiBalanceType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiAmount;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
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
    return mapAccountToSpiAccount(account, false, null);
  }

  /**
   * Maps TestData Account to SpiAccount.
   *
   * @param account         TestData Account
   * @param withBalance     optional Query Parameter
   * @param consentBalances list of balances within consent
   * @return SpiAccount
   */
  public SpiAccountDetails mapAccountToSpiAccount(Account account, boolean withBalance,
      List<SpiAccountReference> consentBalances) {
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
        withBalance
            ? mapBalanceListToSpiBalanceList(account, consentBalances)
            : null);
  }

  /**
   * Maps TestData Transaction to SpiTransaction.
   *
   * @param transaction TestData Transaction
   * @return SpiTransaction
   */
  public SpiTransaction mapTransactionToSpiTransaction(Transaction transaction) {
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
            this.mapAccountToSpiAccount(this.getAccountData(transaction.getCreditorAccount()))),
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
   * Maps TestData Balance List to SpiAccountBalance List.
   *
   * @param account         account object to be mapped
   * @param consentBalances list of balances within consent
   * @return SpiAccountBalance List
   */
  public List<SpiAccountBalance> mapBalanceListToSpiBalanceList(Account account,
      List<SpiAccountReference> consentBalances) {

    if (!isBalanceAccessAllowed(account, consentBalances)) {
      return new ArrayList<>();
    }

    List<Balance> balances = account.getBalances();
    List<SpiAccountBalance> spiAccountBalances = new ArrayList<>();
    for (Balance balance : balances) {
      SpiAccountBalance spiBalance = new SpiAccountBalance();
      SpiAmount spiAmount = new SpiAmount(balance.getBalanceAmount().getCurrency(),
          balance.getBalanceAmount().getAmount());
      spiBalance.setSpiBalanceAmount(spiAmount);
      spiBalance.setSpiBalanceType(SpiBalanceType.valueOf(balance.getBalanceType().name()));
      spiAccountBalances.add(spiBalance);
    }
    return spiAccountBalances;
  }

  private boolean isBalanceAccessAllowed(Account account,
      List<SpiAccountReference> consentBalances) {
    return consentBalances.stream()
        .anyMatch(balance -> Objects.equals(balance.getIban(), account.getIban()));
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
