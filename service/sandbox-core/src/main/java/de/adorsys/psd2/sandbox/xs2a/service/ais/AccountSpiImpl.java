package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataMapper;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import de.adorsys.psd2.xs2a.core.ais.BookingStatus;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.core.error.TppMessage;
import de.adorsys.psd2.xs2a.spi.domain.SpiAspspConsentDataProvider;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransactionReport;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.AccountSpi;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AccountSpiImpl implements AccountSpi {

  private TestDataService testDataService;
  private TestDataMapper testDataMapper;

  public AccountSpiImpl(TestDataService testDataService, TestDataMapper testDataMapper) {
    this.testDataService = testDataService;
    this.testDataMapper = testDataMapper;
  }

  @Override
  public SpiResponse<List<SpiAccountDetails>> requestAccountList(
      @NotNull SpiContextData ctx,
      boolean withBalance,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    Optional<List<SpiAccountDetails>> spiAccountDetails = getAccountDetails(spiAccountConsent,
        withBalance);

    if (!spiAccountDetails.isPresent()) {
      return SpiResponse.<List<SpiAccountDetails>>builder()
          .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_UNKNOWN_ACCOUNT,"Account not found"))
          .build();
    }

    return SpiResponse.<List<SpiAccountDetails>>builder()
        .payload(spiAccountDetails.get())
        .build();
  }

  @Override
  public SpiResponse<SpiAccountDetails> requestAccountDetailForAccount(
      @NotNull SpiContextData ctx,
      boolean withBalance,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    Optional<List<SpiAccountDetails>> spiAccountDetails = getAccountDetails(spiAccountConsent,
        withBalance);

    if (!spiAccountDetails.isPresent()) {
      return SpiResponse.<SpiAccountDetails>builder()
                 .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_UNKNOWN_ACCOUNT,
                     "Account not found"))
                 .build();
    }

    return SpiResponse.<SpiAccountDetails>builder()
        .payload(spiAccountDetails.get().get(0))
        .build();
  }

  @Override
  public SpiResponse<SpiTransactionReport> requestTransactionsForAccount(
      @NotNull SpiContextData spiContextData,
      String acceptHeader,
      boolean withBalance,
      @NotNull LocalDate dateFrom,
      @NotNull LocalDate dateTo,
      @NotNull BookingStatus bookingStatus,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    Optional<List<Transaction>> transactions = this.getTransactionsByIbanAndAccountId(
        spiAccountReference.getIban(),
        spiAccountReference.getResourceId());

    if (!transactions.isPresent()) {
      return SpiResponse.<SpiTransactionReport>builder()
                 .error(new TppMessage(MessageErrorCode.FORMAT_ERROR,
                     "No transactions found for current account"))
                 .build();
    }

    List<SpiTransaction> spiTransactions = transactions.get().stream()
        .filter(t -> this.dateIsInDateRange(t.getBookingDate(), dateFrom, dateTo))
        .filter(t -> this.isTransactionWithBookingStatus(t, bookingStatus))
        .map(testDataMapper::mapTransactionToSpiTransaction)
        .collect(Collectors.toList());

    List<SpiAccountBalance> balances = null;
    if (withBalance) {
      Pair<List<SpiAccountBalance>, SpiResponse<SpiTransactionReport>> rslt = getBalancesForAccount(
          spiAccountReference,
          spiAccountConsent
      );
      if (rslt.getRight() != null) {
        return rslt.getRight();
      }
      balances = rslt.getLeft();
    }

    SpiTransactionReport spiTransactionReport = new SpiTransactionReport(
        null,
        spiTransactions,
        balances,
        SpiTransactionReport.RESPONSE_TYPE_JSON,
        null);

    return SpiResponse.<SpiTransactionReport>builder()
        .payload(spiTransactionReport)
        .build();
  }

  @Override
  public SpiResponse<SpiTransaction> requestTransactionForAccountByTransactionId(
      @NotNull SpiContextData ctx,
      @NotNull String transactionId,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    Optional<List<Transaction>> transactions = this.getTransactionsByIbanAndAccountId(
        spiAccountReference.getIban(),
        spiAccountReference.getResourceId());

    if (!transactions.isPresent()) {
      return SpiResponse.<SpiTransaction>builder()
                 .error(new TppMessage(MessageErrorCode.FORMAT_ERROR,
                     "No transactions found for current account"))
                 .build();
    }

    SpiTransaction spiTransaction = transactions.get().stream()
        .filter(t -> t.getTransactionId().equals(transactionId))
        .map(testDataMapper::mapTransactionToSpiTransaction)
        .findFirst()
        .get();

    return SpiResponse.<SpiTransaction>builder()
        .payload(spiTransaction)
        .build();
  }

  @Override
  public SpiResponse<List<SpiAccountBalance>> requestBalancesForAccount(
      @NotNull SpiContextData ctx,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    Pair<List<SpiAccountBalance>, SpiResponse<List<SpiAccountBalance>>> res = getBalancesForAccount(
        spiAccountReference,
        spiAccountConsent
    );
    if (res.getRight() != null) {
      return res.getRight();
    }

    return SpiResponse.<List<SpiAccountBalance>>builder()
        .payload(res.getLeft())
        .build();
  }

  private Optional<List<SpiAccountDetails>> getAccountDetails(SpiAccountConsent spiAccountConsent,
      boolean withBalance) {
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
        .map(account -> testDataMapper.mapAccountToSpiAccount(account, withBalance,
            spiAccountConsent.getAccess().getBalances()))
        .collect(Collectors.toList());

    return Optional.of(spiAccountDetails);
  }

  private Optional<List<Transaction>> getTransactionsByIbanAndAccountId(String iban,
      String accountId) {
    Optional<TestPsu> psu = testDataService.getPsuByIban(iban);

    if (!psu.isPresent()) {
      return Optional.empty();
    }

    return testDataService
        .getTransactions(psu.get().getPsuId(), accountId);
  }

  /*
   * @return Pair<Result, Error> with either Result or Error set
   */
  private <T> Pair<List<SpiAccountBalance>, SpiResponse<T>> getBalancesForAccount(
      SpiAccountReference accountReference, SpiAccountConsent spiAccountConsent
  ) {
    Optional<TestPsu> psu = testDataService.getPsuByIban(accountReference.getIban());

    if (!psu.isPresent()) {
      return Pair.of(null, SpiResponse.<T>builder()
          .error(new TppMessage(MessageErrorCode.PSU_CREDENTIALS_INVALID,"User not found"))
          .build()
      );
    }

    Optional<Account> account = testDataService.getDistinctAccount(
        psu.get().getPsuId(),
        accountReference.getResourceId()
    );

    if (!account.isPresent()) {
      return Pair.of(null, SpiResponse.<T>builder()
                               .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_UNKNOWN_ACCOUNT,
                                   "Account not found"))
                               .build());
    }

    return Pair.of(
        testDataMapper.mapBalanceListToSpiBalanceList(
            account.get(), spiAccountConsent.getAccess().getBalances()
        ),
        null
    );
  }

  boolean dateIsInDateRange(LocalDate date, LocalDate from, LocalDate to) {
    return date == null || date.isAfter(from) && date.isBefore(to)
        || date.isEqual(from) || date.isEqual(to);
  }

  boolean isTransactionWithBookingStatus(Transaction transaction, BookingStatus bookingStatus) {
    if (bookingStatus.equals(BookingStatus.BOOKED)) {
      return transaction.getBookingDate() != null;
    } else if (bookingStatus.equals(BookingStatus.PENDING)) {
      return transaction.getBookingDate() == null;
    }
    return true;
  }
}
