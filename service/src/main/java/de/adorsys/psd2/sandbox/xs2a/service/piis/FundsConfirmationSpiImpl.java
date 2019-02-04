package de.adorsys.psd2.sandbox.xs2a.service.piis;

import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.portal.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.piis.PiisConsent;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.fund.SpiFundsConfirmationRequest;
import de.adorsys.psd2.xs2a.spi.domain.fund.SpiFundsConfirmationResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.FundsConfirmationSpi;
import java.math.BigDecimal;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class FundsConfirmationSpiImpl implements FundsConfirmationSpi {

  private TestDataService testDataService;

  public FundsConfirmationSpiImpl(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  @Override
  public @NotNull SpiResponse<SpiFundsConfirmationResponse> performFundsSufficientCheck(
      @NotNull SpiContextData ctx,
      @Nullable PiisConsent piisConsent,
      @NotNull SpiFundsConfirmationRequest spiFundsConfirmationRequest,
      @NotNull AspspConsentData aspspConsentData) {
    SpiFundsConfirmationResponse response = new SpiFundsConfirmationResponse();

    String iban = spiFundsConfirmationRequest.getPsuAccount().getIban();

    Optional<TestPsu> psuId = testDataService.getPsuByIban(iban);

    // Passed iban could not be matched to an existing PSU
    if (!psuId.isPresent()) {
      response.setFundsAvailable(false);
      return new SpiResponse<>(response, aspspConsentData);
    }

    Optional<String> accountId = testDataService.getAccountIdByIban(psuId.get().getPsuId(), iban);
    BigDecimal requestedAmount = spiFundsConfirmationRequest.getInstructedAmount().getAmount();

    if (accountId.isPresent()) {
      Optional<Account> account = testDataService.getDistinctAccount(
          psuId.get().getPsuId(), accountId.get());
      if (account.isPresent()) {
        Balance balance = account.get().getAvailableBalance();
        response
            .setFundsAvailable(
                requestedAmount.compareTo(balance.getBalanceAmount().getAmount()) <= 0);
      }
    }
    return new SpiResponse<>(response, aspspConsentData);
  }
}
