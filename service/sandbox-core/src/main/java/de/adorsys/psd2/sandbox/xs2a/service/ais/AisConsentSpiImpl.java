package de.adorsys.psd2.sandbox.xs2a.service.ais;

import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.exception.RestException;
import de.adorsys.psd2.xs2a.spi.domain.SpiAspspConsentDataProvider;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.consent.SpiInitiateAisConsentResponse;
import de.adorsys.psd2.xs2a.spi.domain.consent.SpiVerifyScaAuthorisationResponse;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.AisConsentSpi;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AisConsentSpiImpl implements AisConsentSpi {

  private AuthorisationService authorisationService;
  private TestDataService testDataService;

  @Autowired
  public AisConsentSpiImpl(AuthorisationService authorisationService,
                           TestDataService testDataService) {
    this.authorisationService = authorisationService;
    this.testDataService = testDataService;
  }

  @Override
  public SpiResponse<SpiInitiateAisConsentResponse> initiateAisConsent(
      @NotNull SpiContextData spiContextData,
      SpiAccountConsent spiAccountConsent,
      SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    if (!spiAccountConsent.getAccess().getAccounts().isEmpty()) {
      // TODO potential bug - we need to check ALL IBANs here? (rat)
      Optional<TestPsu> psuId = testDataService
          .getPsuByIban(spiAccountConsent.getAccess().getAccounts().get(0).getIban());

      // TODO what's the right error here? (rat)
      TestPsu knownPsuId = psuId
                               .orElseThrow(() -> new RestException(MessageErrorCode.FORMAT_ERROR,
                                   "User not found"));

      if (testDataService.isBlockedPsu(knownPsuId.getPsuId())) {
        throw new RestException(MessageErrorCode.SERVICE_BLOCKED,"Account is BLOCKED by ASPSP");
      }

      spiAccountConsent.getAccess().getAccounts()
          .forEach(ar -> checkAccountIsPresentAndCurrencyCorrect(knownPsuId, ar));
    }
    return SpiResponse.<SpiInitiateAisConsentResponse>builder()
        .payload(new SpiInitiateAisConsentResponse())
        .build();
  }

  private void checkAccountIsPresentAndCurrencyCorrect(TestPsu knownPsuId, SpiAccountReference ar) {
    Account account = testDataService.getAccountByIban(knownPsuId.getPsuId(), ar.getIban())
                          .orElseThrow(() -> new RestException(MessageErrorCode.FORMAT_ERROR,
                              "Account not found"));
    checkCurrency(ar.getCurrency(), account.getCurrency());
  }

  private void checkCurrency(Currency expectedCurrency, Currency definedCurrency) {
    if (expectedCurrency != null && !definedCurrency.equals(expectedCurrency)) {
      throw new RestException(MessageErrorCode.FORMAT_ERROR,"Account not found");
    }
  }

  @Override
  public SpiResponse<SpiResponse.VoidResponse> revokeAisConsent(
      @NotNull SpiContextData spiContextData,
      SpiAccountConsent spiAccountConsent,
      SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return SpiResponse.<SpiResponse.VoidResponse>builder()
               .payload(SpiResponse.voidResponse())
               .build();
  }

  @Override
  public @NotNull SpiResponse<SpiVerifyScaAuthorisationResponse> verifyScaAuthorisation(
      @NotNull SpiContextData spiContextData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return SpiResponse.<SpiVerifyScaAuthorisationResponse>builder()
        .payload(new SpiVerifyScaAuthorisationResponse(spiAccountConsent.getConsentStatus()))
        .build();
  }

  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      @NotNull SpiContextData spiContextData,
      @NotNull SpiPsuData spiPsuData,
      String password,
      SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    String iban = spiAccountConsent.getAccess().getAccounts().get(0).getIban();

    return authorisationService.authorisePsu(spiPsuData, password, iban, false);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiContextData spiContextData,
      SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    return authorisationService.requestAvailableScaMethods();
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiContextData spiContextData,
      @NotNull String selectedScaMethod,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    return authorisationService.requestAuthorisationCode(selectedScaMethod);
  }
}
