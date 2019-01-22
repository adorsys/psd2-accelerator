package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.consent.SpiInitiateAisConsentResponse;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.AisConsentSpi;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AisConsentSpiImpl implements AisConsentSpi {

  private AuthorisationService authorisationService;

  @Autowired
  public AisConsentSpiImpl(AuthorisationService authorisationService) {
    this.authorisationService = authorisationService;
  }

  @Override
  public SpiResponse<SpiInitiateAisConsentResponse> initiateAisConsent(
      @NotNull SpiContextData spiContextData,
      SpiAccountConsent spiAccountConsent,
      AspspConsentData aspspConsentData) {

    return new SpiResponse<>(new SpiInitiateAisConsentResponse(), aspspConsentData);
  }

  @Override
  public SpiResponse<SpiResponse.VoidResponse> revokeAisConsent(
      @NotNull SpiContextData spiContextData,
      SpiAccountConsent spiAccountConsent,
      AspspConsentData aspspConsentData) {
    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisation(
      @NotNull SpiContextData spiContextData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      @NotNull SpiContextData spiContextData,
      @NotNull SpiPsuData spiPsuData,
      String password,
      SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    String iban = spiAccountConsent.getAccess().getAccounts().get(0).getIban();

    return authorisationService.authorisePsu(spiPsuData, password, iban, aspspConsentData, false);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiContextData spiContextData,
      SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return authorisationService.requestAvailableScaMethods(aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiContextData spiContextData,
      @NotNull String selectedScaMethod,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {

    return authorisationService.requestAuthorisationCode(selectedScaMethod, aspspConsentData);
  }
}
