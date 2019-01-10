package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AccountSpiImpl implements AccountSpi {

  @Override
  public SpiResponse<List<SpiAccountDetails>> requestAccountList(boolean b,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    // TODO instead of returning an empty List, we need to get the response-data from our
    // TestDataService
    return SpiResponse.<List<SpiAccountDetails>>builder()
        .aspspConsentData(aspspConsentData)
        .payload(new ArrayList<>())
        .success();
  }

  @Override
  public SpiResponse<SpiAccountDetails> requestAccountDetailForAccount(boolean b,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<SpiAccountDetails>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  @Override
  public SpiResponse<SpiTransactionReport> requestTransactionsForAccount(String s, boolean b,
      @NotNull LocalDate localDate, @NotNull LocalDate localDate1,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent, @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<SpiTransactionReport>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  @Override
  public SpiResponse<SpiTransaction> requestTransactionForAccountByTransactionId(
      @NotNull String s, @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<SpiTransaction>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }

  @Override
  public SpiResponse<List<SpiAccountBalance>> requestBalancesForAccount(
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return SpiResponse.<List<SpiAccountBalance>>builder()
        .aspspConsentData(aspspConsentData)
        .success();
  }
}
