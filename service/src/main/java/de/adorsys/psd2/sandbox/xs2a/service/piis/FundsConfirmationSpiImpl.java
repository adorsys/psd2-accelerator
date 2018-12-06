package de.adorsys.psd2.sandbox.xs2a.service.piis;

import de.adorsys.psd2.sandbox.testdata.TestDataService;
import de.adorsys.psd2.sandbox.testdata.domain.Account;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.fund.SpiFundsConfirmationRequest;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.FundsConfirmationSpi;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class FundsConfirmationSpiImpl implements FundsConfirmationSpi {

  private TestDataService testDataService = new TestDataService();

  @Override
  public @NotNull SpiResponse<Boolean> performFundsSufficientCheck(
      @NotNull SpiPsuData spiPsuData,
      @Nullable String s,
      @NotNull SpiFundsConfirmationRequest spiFundsConfirmationRequest,
      @NotNull AspspConsentData aspspConsentData) {

    Account account = testDataService.getAccountDetails(TestDataService.ACCOUNT_ID_GIRO);

    String requestedIban = spiFundsConfirmationRequest.getPsuAccount().getIban();
    BigDecimal requestedAmount = spiFundsConfirmationRequest.getInstructedAmount().getAmount();

    if (account.getIban().equals(requestedIban)) {
      if (hasSufficientFunds(account, requestedAmount)) {
        return new SpiResponse<>(true, aspspConsentData);
      }
    }
    return new SpiResponse<>(false, aspspConsentData);
  }

  private boolean hasSufficientFunds(Account account, BigDecimal amount) {
    return amount.compareTo(account.getAmount()) <= 0;
  }
}
