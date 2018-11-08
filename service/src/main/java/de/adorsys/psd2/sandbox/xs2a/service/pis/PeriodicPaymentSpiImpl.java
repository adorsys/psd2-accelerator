package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PeriodicPaymentSpi;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PeriodicPaymentSpiImpl implements PeriodicPaymentSpi {


  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> executePaymentWithoutSca(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }
}
