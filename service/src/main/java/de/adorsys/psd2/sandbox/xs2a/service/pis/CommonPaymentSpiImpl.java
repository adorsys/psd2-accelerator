package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPaymentInfo;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.CommonPaymentSpi;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class CommonPaymentSpiImpl implements CommonPaymentSpi {

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> executePaymentWithoutSca(
      @NotNull SpiContextData spiContextData, @NotNull SpiPaymentInfo spiPaymentInfo,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiContextData spiContextData, @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPaymentInfo spiPaymentInfo, @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiPaymentInitiationResponse> initiatePayment(
      @NotNull SpiContextData contextData, @NotNull SpiPaymentInfo payment,
      @NotNull AspspConsentData initialAspspConsentData) {
    return null;
  }
}
