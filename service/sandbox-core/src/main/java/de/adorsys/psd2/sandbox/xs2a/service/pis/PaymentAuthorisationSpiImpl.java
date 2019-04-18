package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
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
      @NotNull SpiContextData ctx,
      @NotNull SpiPsuData psuData,
      String password,
      SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {

    String iban = null;

    if (spiPayment instanceof SpiSinglePayment) {
      iban = ((SpiSinglePayment) spiPayment).getDebtorAccount().getIban();
    }
    if (spiPayment instanceof SpiPeriodicPayment) {
      iban = ((SpiPeriodicPayment) spiPayment).getDebtorAccount().getIban();
    }

    return authorisationService.authorisePsu(
        ctx.getPsuData(), password, iban, aspspConsentData, false
    );
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiContextData ctx,
      SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {

    return authorisationService.requestAvailableScaMethods(aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiContextData ctx,
      @NotNull String selectedScaMethod,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {

    return authorisationService.requestAuthorisationCode(selectedScaMethod, aspspConsentData);
  }
}
