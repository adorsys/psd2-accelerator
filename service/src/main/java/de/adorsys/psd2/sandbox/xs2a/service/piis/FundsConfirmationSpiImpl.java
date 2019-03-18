package de.adorsys.psd2.sandbox.xs2a.service.piis;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.BalanceType;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.piis.PiisConsent;
import de.adorsys.psd2.xs2a.domain.MessageErrorCode;
import de.adorsys.psd2.xs2a.exception.RestException;
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

    Optional<Account> account = testDataService.getAccountByIban(psuId.get().getPsuId(), iban);
    BigDecimal requestedAmount = spiFundsConfirmationRequest.getInstructedAmount().getAmount();

    if (account.isPresent()) {
      if (!isCorrectCurrency(account.get(), spiFundsConfirmationRequest)) {
        throw new RestException(MessageErrorCode.FORMAT_ERROR);
      }

      Balance balance = account.get().getBalances().stream()
          .filter(b -> b.getBalanceType().equals(BalanceType.INTERIM_AVAILABLE))
          .findFirst().get();

      response
          .setFundsAvailable(
              requestedAmount.compareTo(balance.getBalanceAmount().getAmount()) <= 0);
    }
    return new SpiResponse<>(response, aspspConsentData);
  }

  private boolean isCorrectCurrency(Account account, SpiFundsConfirmationRequest request) {
    if (request.getPsuAccount().getCurrency().equals(account.getCurrency())
        && request.getInstructedAmount().getCurrency().equals(account.getCurrency())) {
      return true;
    }
    return false;
  }
}
