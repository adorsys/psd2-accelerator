package de.adorsys.psd2.sandbox.xs2a.service.piis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.fund.SpiFundsConfirmationRequest;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.FundsConfirmationSpi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class FundsConfirmationSpiImpl implements FundsConfirmationSpi {

  @Override
  public @NotNull SpiResponse<Boolean> performFundsSufficientCheck(
      @NotNull SpiPsuData spiPsuData, @Nullable String s,
      @NotNull SpiFundsConfirmationRequest spiFundsConfirmationRequest,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }
}
