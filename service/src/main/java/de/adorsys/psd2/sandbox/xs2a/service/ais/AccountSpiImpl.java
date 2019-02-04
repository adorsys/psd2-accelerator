package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.portal.testdata.domain.TestPsu;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Transaction;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiBalanceType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransactionReport;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiAmount;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import de.adorsys.psd2.xs2a.spi.service.AccountSpi;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AccountSpiImpl implements AccountSpi {

  private TestDataService testDataService;

  public AccountSpiImpl(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  @Override
  public SpiResponse<List<SpiAccountDetails>> requestAccountList(
      @NotNull SpiContextData ctx,
      boolean b,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    Optional<List<SpiAccountDetails>> spiAccountDetails = getAccountDetails(spiAccountConsent);

    if (!spiAccountDetails.isPresent()) {
      return SpiResponse.<List<SpiAccountDetails>>builder()
          .aspspConsentData(aspspConsentData)
          .fail(SpiResponseStatus.TECHNICAL_FAILURE);
    }

    return SpiResponse.<List<SpiAccountDetails>>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiAccountDetails.get())
        .success();
  }

  @Override
  public SpiResponse<SpiAccountDetails> requestAccountDetailForAccount(
      @NotNull SpiContextData ctx,
      boolean b,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    Optional<List<SpiAccountDetails>> spiAccountDetails = getAccountDetails(spiAccountConsent);

    if (!spiAccountDetails.isPresent()) {
      return SpiResponse.<SpiAccountDetails>builder()
          .aspspConsentData(aspspConsentData)
          .fail(SpiResponseStatus.TECHNICAL_FAILURE);
    }

    return SpiResponse.<SpiAccountDetails>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiAccountDetails.get().get(0))
        .success();
  }

  @Override
  public SpiResponse<SpiTransactionReport> requestTransactionsForAccount(
      @NotNull SpiContextData ctx,
      String acceptHeader,
      boolean withBalance,
      @NotNull LocalDate localDate,
      @NotNull LocalDate localDate1,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    Optional<TestPsu> psu = testDataService.getPsuByIban(spiAccountReference.getIban());

    if (!psu.isPresent()) {
      return null;
    }

    Optional<List<Transaction>> transactions = testDataService
        .getTransactions(psu.get().getPsuId(), spiAccountReference.getResourceId());

    if (!transactions.isPresent()) {
      return null;
    }

    List<SpiTransaction> spiTransactions = transactions.get().stream()
        .map(this::mapTransactionToSpiTransaction)
        .collect(Collectors.toList());

    SpiTransactionReport spiTransactionReport = new SpiTransactionReport(
        spiTransactions,
        null,
        SpiTransactionReport.RESPONSE_TYPE_JSON,
        null);

    return SpiResponse.<SpiTransactionReport>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiTransactionReport)
        .success();
  }

  @Override
  public SpiResponse<SpiTransaction> requestTransactionForAccountByTransactionId(
      @NotNull SpiContextData ctx,
      @NotNull String transactionId,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    Optional<TestPsu> psu = testDataService.getPsuByIban(spiAccountReference.getIban());

    if (!psu.isPresent()) {
      return null;
    }

    Optional<List<Transaction>> transactions = testDataService
        .getTransactions(psu.get().getPsuId(), spiAccountReference.getResourceId());

    if (!transactions.isPresent()) {
      return null;
    }

    Optional<SpiTransaction> spiTransaction = transactions.get().stream()
        .filter(t -> t.getTransactionId().equals(transactionId))
        .map(this::mapTransactionToSpiTransaction)
        .findFirst();

    return SpiResponse.<SpiTransaction>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiTransaction.get())
        .success();
  }

  @Override
  public SpiResponse<List<SpiAccountBalance>> requestBalancesForAccount(
      @NotNull SpiContextData ctx,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<List<SpiAccountBalance>>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  private Optional<List<SpiAccountDetails>> getAccountDetails(SpiAccountConsent spiAccountConsent) {
    List<SpiAccountReference> accountList = spiAccountConsent.getAccess().getAccounts();
    List<String> ibans = new ArrayList<>();

    for (SpiAccountReference account : accountList) {
      ibans.add(account.getIban());
    }

    Optional<TestPsu> optionalPsuId = testDataService.getPsuByIban(ibans.get(0));

    if (!optionalPsuId.isPresent()) {
      return Optional.empty();
    }

    Optional<List<Account>> optionalAccounts = testDataService
        .getRequestedAccounts(optionalPsuId.get().getPsuId(), ibans);

    if (!optionalAccounts.isPresent()) {
      return Optional.empty();
    }

    List<SpiAccountDetails> spiAccountDetails = optionalAccounts.get().stream()
        .map(this::mapAccountToSpiAccount)
        .collect(Collectors.toList());

    return Optional.of(spiAccountDetails);
  }

  private SpiAccountDetails mapAccountToSpiAccount(Account account) {
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

  private SpiTransaction mapTransactionToSpiTransaction(
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
            this.mapAccountToSpiAccount(getAccountData(transaction.getCreditorAccount()))),
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

  private SpiAccountBalance mapBalanceToSpiBalance(Balance balance) {
    SpiAccountBalance spiAccountBalance = new SpiAccountBalance();

    SpiAmount spiAmount = new SpiAmount(balance.getBalanceAmount().getCurrency(),
        balance.getBalanceAmount().getAmount());
    spiAccountBalance.setSpiBalanceAmount(spiAmount);
    spiAccountBalance.setSpiBalanceType(SpiBalanceType.AVAILABLE);

    return spiAccountBalance;
  }

  private Account getAccountData(String iban) {
    Optional<TestPsu> psuId = testDataService.getPsuByIban(iban);

    if (!psuId.isPresent()) {
      return new Account(iban, Currency.getInstance("EUR"));
    }

    Optional<Account> account = testDataService.getDistinctAccount(psuId.get().getPsuId(), iban);

    return account.orElseGet(() -> new Account(iban, Currency.getInstance("EUR")));

  }


}
