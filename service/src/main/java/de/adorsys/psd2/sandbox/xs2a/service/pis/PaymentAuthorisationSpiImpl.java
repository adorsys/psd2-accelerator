package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentAuthorisationSpiImpl implements PaymentAuthorisationSpi {

  private AuthorisationService authorisationService;

  @Autowired
  public PaymentAuthorisationSpiImpl(AuthorisationService authorisationService) {
    this.authorisationService = authorisationService;
  }

  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      @NotNull SpiPsuData spiPsuData,
      String password,
      SpiPayment spiPayment,
      AspspConsentData aspspConsentData) {

    return authorisationService.authorisePsu(spiPsuData, password, aspspConsentData);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiPsuData spiPsuData,
      SpiPayment spiPayment,
      AspspConsentData aspspConsentData) {

    return authorisationService.requestAvailableScaMethods(aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiPsuData spiPsuData,
      @NotNull String selectedScaMethod,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {

    return authorisationService.requestAuthorisationCode(selectedScaMethod, aspspConsentData);
  }
}
