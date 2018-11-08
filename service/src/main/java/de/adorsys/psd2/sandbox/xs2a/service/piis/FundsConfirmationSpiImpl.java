package de.adorsys.psd2.sandbox.xs2a.service.piis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiAmount;
import de.adorsys.psd2.xs2a.spi.domain.fund.SpiFundsConfirmationConsent;
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
      @NotNull SpiPsuData spiPsuData,
      @Nullable SpiFundsConfirmationConsent spiFundsConfirmationConsent,
      @NotNull SpiAccountReference spiAccountReference,
      @NotNull SpiAmount spiAmount, @NotNull AspspConsentData aspspConsentData) {
    return null;
  }
}
