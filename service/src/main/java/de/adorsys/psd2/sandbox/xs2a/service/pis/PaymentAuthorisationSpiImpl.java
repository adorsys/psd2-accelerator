package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PaymentAuthorisationSpi;
import de.adorsys.psd2.xs2a.spi.service.SpiPayment;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PaymentAuthorisationSpiImpl implements PaymentAuthorisationSpi {


  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(@NotNull SpiPsuData spiPsuData,
      String password, SpiPayment spiPayment, AspspConsentData aspspConsentData) {

    return null;
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiPsuData spiPsuData, SpiPayment spiPayment,
      AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiPsuData spiPsuData, @NotNull String s,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }
}
