package de.adorsys.psd2.sandbox.xs2a.service.ais;

import static de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.FAILURE;
import static de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.SUCCESS;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.psd2.sandbox.xs2a.service.domain.SpiAspspAuthorisationData;
import de.adorsys.psd2.xs2a.component.JsonConverter;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.sca.ChallengeData;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountConsent;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import de.adorsys.psd2.xs2a.spi.service.AisConsentSpi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AisConsentSpiImpl implements AisConsentSpi {

  private static final String PSU_ID = "PSU-1";
  private static final String PSU_PASSWORD = "12345";
  private final JsonConverter jsonConverter = new JsonConverter(new ObjectMapper());

  @Override
  public SpiResponse<SpiResponse.VoidResponse> initiateAisConsent(
      @NotNull SpiPsuData spiPsuData, SpiAccountConsent spiAccountConsent,
      AspspConsentData aspspConsentData) {

    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public SpiResponse<SpiResponse.VoidResponse> revokeAisConsent(
      @NotNull SpiPsuData spiPsuData, SpiAccountConsent spiAccountConsent,
      AspspConsentData aspspConsentData) {
    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisation(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      @NotNull SpiPsuData spiPsuData,
      String password,
      SpiAccountConsent spiAccountConsent,
      AspspConsentData aspspConsentData) {
    Optional<SpiAspspAuthorisationData> accessToken = Optional.of(new SpiAspspAuthorisationData(
        PSU_ID, PSU_PASSWORD));

    byte[] payload = accessToken.flatMap(jsonConverter::toJson)
        .map(String::getBytes)
        .orElse(null);

    if (spiPsuData.getPsuId().equals(PSU_ID) && password.equals(PSU_PASSWORD)) {
      return SpiResponse.<SpiAuthorisationStatus>builder()
          .aspspConsentData(aspspConsentData.respondWith(payload))
          .payload(SUCCESS)
          .success();
    }

    return SpiResponse.<SpiAuthorisationStatus>builder()
        .aspspConsentData(aspspConsentData.respondWith(payload))
        .payload(FAILURE)
        .fail(SpiResponseStatus.UNAUTHORIZED_FAILURE);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiPsuData spiPsuData, SpiAccountConsent spiAccountConsent,
      AspspConsentData aspspConsentData) {
    SpiAuthenticationObject smsTan = new SpiAuthenticationObject();
    smsTan.setName("SMS_OTP");
    smsTan.setAuthenticationMethodId("SMS_OTP");
    SpiAuthenticationObject pushTan = new SpiAuthenticationObject();
    pushTan.setName("PUSH_OTP");
    pushTan.setAuthenticationMethodId("PUSH_OTP");
    List<SpiAuthenticationObject> spiMethods = new ArrayList<>(Arrays.asList(pushTan, smsTan));

    return SpiResponse.<List<SpiAuthenticationObject>>builder()
        .aspspConsentData(aspspConsentData.respondWith("TEST ASPSP DATA".getBytes()))
        .payload(spiMethods)
        .success();
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiPsuData spiPsuData,
      @NotNull String selectedScaMethod,
      @NotNull SpiAccountConsent spiAccountConsent,
      @NotNull AspspConsentData aspspConsentData) {
    SpiAuthorizationCodeResult result = new SpiAuthorizationCodeResult();
    SpiAuthenticationObject selected = new SpiAuthenticationObject();
    selected.setAuthenticationMethodId(selectedScaMethod);
    result.setSelectedScaMethod(selected);
    result.setChallengeData(new ChallengeData()); // NPE otherwise
    return new SpiResponse<>(result, aspspConsentData);
  }
}
