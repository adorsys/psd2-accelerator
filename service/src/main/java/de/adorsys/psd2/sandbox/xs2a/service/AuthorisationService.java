package de.adorsys.psd2.sandbox.xs2a.service;

import static de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.SUCCESS;

import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.sandbox.portal.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.sca.ChallengeData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthorisationService {

  private TestDataService testDataService;

  public AuthorisationService(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  /**
   * Abstract Implementation of requestAvailableScaMethods.
   *
   * @param spiPsuData       spiPsuData
   * @param password         password
   * @param aspspConsentData aspspConsentData
   */
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      SpiPsuData spiPsuData, String password, String iban, AspspConsentData aspspConsentData,
      boolean forceFailure) {

    Optional<TestPsu> inquiringPsu = testDataService.getPsu(spiPsuData.getPsuId());
    Optional<TestPsu> accOwner = testDataService.getPsuByIban(iban);

    if (!inquiringPsu.isPresent() || !password.equals(inquiringPsu.get().getPassword())
        || !accOwner.isPresent() || !inquiringPsu.get().getPsuId().equals(accOwner.get().getPsuId())
        || inquiringPsu.get().getPsuId().equals("PSU-Rejected")) {
      return SpiResponse.<SpiAuthorisationStatus>builder()
          .aspspConsentData(aspspConsentData)
          .message(Collections.singletonList("Authorization failed"))
          .fail(SpiResponseStatus.UNAUTHORIZED_FAILURE);
    }

    if (inquiringPsu.get().getPsuId().equals("PSU-Cancellation-Rejected")
        && forceFailure) {
      return SpiResponse.<SpiAuthorisationStatus>builder()
          .aspspConsentData(aspspConsentData)
          .message(Collections.singletonList("Authorization failed"))
          .fail(SpiResponseStatus.UNAUTHORIZED_FAILURE);
    }

    return SpiResponse.<SpiAuthorisationStatus>builder()
        .aspspConsentData(aspspConsentData)
        .payload(SUCCESS)
        .success();
  }

  /**
   * Abstract Implementation of requestAvailableScaMethods.
   *
   * @param aspspConsentData aspspConsentData
   */
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      AspspConsentData aspspConsentData) {
    SpiAuthenticationObject smsTan = new SpiAuthenticationObject();
    smsTan.setName("SMS_OTP");
    smsTan.setAuthenticationMethodId("SMS_OTP");
    SpiAuthenticationObject pushTan = new SpiAuthenticationObject();
    pushTan.setName("PUSH_OTP");
    pushTan.setAuthenticationMethodId("PUSH_OTP");
    List<SpiAuthenticationObject> spiMethods = new ArrayList<>(Arrays.asList(pushTan, smsTan));

    return SpiResponse.<List<SpiAuthenticationObject>>builder()
        .aspspConsentData(aspspConsentData)
        .payload(spiMethods)
        .success();
  }

  /**
   * Abstract Implementation of requestAuthorisationCode.
   *
   * @param selectedScaMethod selectedScaMethod
   * @param aspspConsentData  aspspConsentData
   */
  public SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      String selectedScaMethod, AspspConsentData aspspConsentData) {
    SpiAuthorizationCodeResult result = new SpiAuthorizationCodeResult();
    SpiAuthenticationObject selected = new SpiAuthenticationObject();
    selected.setAuthenticationMethodId(selectedScaMethod);
    result.setSelectedScaMethod(selected);
    result.setChallengeData(new ChallengeData()); // NPE otherwise
    return new SpiResponse<>(result, aspspConsentData);
  }
}
