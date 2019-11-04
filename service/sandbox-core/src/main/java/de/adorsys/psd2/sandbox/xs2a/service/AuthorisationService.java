package de.adorsys.psd2.sandbox.xs2a.service;

import static de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus.SUCCESS;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.core.error.TppMessage;
import de.adorsys.psd2.xs2a.core.sca.ChallengeData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import java.util.ArrayList;
import java.util.Arrays;
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
   */
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(
      SpiPsuData spiPsuData, String password, String iban, boolean forceFailure) {

    Optional<TestPsu> inquiringPsu = testDataService.getPsu(spiPsuData.getPsuId());
    Optional<TestPsu> accOwner = testDataService.getPsuByIban(iban);

    if (!inquiringPsu.isPresent() || !password.equals(inquiringPsu.get().getPassword())
        || !accOwner.isPresent() || !inquiringPsu.get().getPsuId().equals(accOwner.get().getPsuId())
        || inquiringPsu.get().getPsuId().equals("PSU-Rejected")) {
      return SpiResponse.<SpiAuthorisationStatus>builder()
          .error(new TppMessage(MessageErrorCode.UNAUTHORIZED,"Authorization failed"))
          .build();
    }

    if (inquiringPsu.get().getPsuId().equals("PSU-Cancellation-Rejected")
        && forceFailure) {
      return SpiResponse.<SpiAuthorisationStatus>builder()
          .error(new TppMessage(MessageErrorCode.UNAUTHORIZED,"Authorization failed"))
          .build();
    }

    return SpiResponse.<SpiAuthorisationStatus>builder()
        .payload(SUCCESS)
        .build();
  }

  /**
   * Abstract Implementation of requestAvailableScaMethods.
   *
   */
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods() {
    SpiAuthenticationObject smsTan = new SpiAuthenticationObject();
    smsTan.setName("SMS_OTP");
    smsTan.setAuthenticationMethodId("SMS_OTP");
    SpiAuthenticationObject pushTan = new SpiAuthenticationObject();
    pushTan.setName("PUSH_OTP");
    pushTan.setAuthenticationMethodId("PUSH_OTP");
    List<SpiAuthenticationObject> spiMethods = new ArrayList<>(Arrays.asList(pushTan, smsTan));

    return SpiResponse.<List<SpiAuthenticationObject>>builder()
        .payload(spiMethods)
        .build();
  }

  /**
   * Abstract Implementation of requestAuthorisationCode.
   *
   * @param selectedScaMethod selectedScaMethod
   */
  public SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      String selectedScaMethod) {
    SpiAuthorizationCodeResult result = new SpiAuthorizationCodeResult();
    SpiAuthenticationObject selected = new SpiAuthenticationObject();
    selected.setAuthenticationMethodId(selectedScaMethod);
    result.setSelectedScaMethod(selected);
    result.setChallengeData(new ChallengeData()); // NPE otherwise
    return SpiResponse.<SpiAuthorizationCodeResult>builder()
        .payload(result)
        .build();
  }
}
