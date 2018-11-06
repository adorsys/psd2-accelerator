package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiBalanceReport;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransactionReport;
import de.adorsys.psd2.xs2a.spi.domain.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.AccountSpi;
import java.time.LocalDate;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AccountSpiImpl implements AccountSpi {

  @Override
  public SpiResponse<List<SpiAccountDetails>> requestAccountDetails(boolean b,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public SpiResponse<SpiAccountDetails> requestAccountDetailForAccount(
      @NotNull String s,
      boolean b,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public SpiResponse<SpiTransactionReport> requestTransactionsForAccount(
      @NotNull String s,
      boolean b,
      @NotNull LocalDate localDate,
      @NotNull LocalDate localDate1,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public SpiResponse<SpiTransaction> requestTransactionForAccountByTransactionId(
      @NotNull String s,
      @NotNull String s1,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public SpiResponse<SpiBalanceReport> requestBalancesForAccount(
      @NotNull String s,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }
}
