package de.adorsys.psd2.sandbox.xs2a.service.redirect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nitorcreations.junit.runners.NestedRunner;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.domain.account.AisConsent;
import de.adorsys.psd2.consent.domain.account.AisConsentAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.repository.AisConsentAuthorisationRepository;
import de.adorsys.psd2.consent.repository.PisAuthorisationRepository;
import de.adorsys.psd2.consent.repository.PisCommonPaymentDataRepository;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(NestedRunner.class)
public class RedirectServiceTest {

  private static final String EXTERNAL_ID = "123";
  private static final String UNSUCCESSFUL_SCA_URI = "http://tpp.de/unsuccessfulsca";
  private static final String SUCCESSFUL_SCA_URI = "http://tpp.de/success";
  private static final String PSU_ID = "PSU_Successful";

  private PisAuthorization pisAuth;
  private AisConsentAuthorization aisAuth;
  private TppInfoEntity tppInfo;
  private RedirectService redirectService;

  @Mock
  private PisAuthorisationRepository pisAuthorizationRepository;

  @Mock
  private AisConsentAuthorisationRepository aisConsentAuthorizationRepository;

  @Mock
  private PisCommonPaymentDataRepository pisCommonPaymentDataRepository;

  @Mock
  private TestDataService testDataService;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    redirectService = new RedirectService(
        pisAuthorizationRepository,
        aisConsentAuthorizationRepository,
        pisCommonPaymentDataRepository,
        testDataService);

    tppInfo = mock(TppInfoEntity.class);
  }

  public class AisRedirect {

    @Before
    public void setup() {
      aisAuth = mock(AisConsentAuthorization.class);
      AisConsent consent = mock(AisConsent.class);

      when(aisAuth.getConsent()).thenReturn(consent);
      when(consent.getTppInfo()).thenReturn(tppInfo);
      when(aisConsentAuthorizationRepository.findByExternalId(anyString()))
          .thenReturn(Optional.of(aisAuth));
    }

    @Test
    public void shouldReturnUnsuccessfulScaUri() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(aisAuth.getConsent().getConsentStatus()).thenReturn(ConsentStatus.REJECTED);
      when(aisAuth.getTppNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(aisAuth.getTppOkRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingDataForConsent(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/unsuccessfulsca", data.getTppRedirectUri());
      assertEquals("rejected", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulScaUri() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      when(aisAuth.getConsent().getConsentStatus()).thenReturn(ConsentStatus.VALID);
      when(aisAuth.getTppNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(aisAuth.getTppOkRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingDataForConsent(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("valid", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulUriWhenNokNotUriSet() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(aisAuth.getConsent().getConsentStatus()).thenReturn(ConsentStatus.REJECTED);
      when(aisAuth.getTppNokRedirectUri()).thenReturn(null);
      when(aisAuth.getTppOkRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingDataForConsent(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("rejected", data.getResourceStatus());
    }

    @Test
    public void shouldReturnScaHasFailedErrorPage() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      TestPsu psu = mock(TestPsu.class);
      when(testDataService.getPsu(anyString())).thenReturn(Optional.of(psu));
      try {
        redirectService.handleConsentCreationRedirectRequest(EXTERNAL_ID, PSU_ID);
        fail("Expected exception not thrown");
      } catch (Exception e) {
        assertEquals("redirect-uri already called",
            e.getMessage());
      }
    }

  }

  public class PisRedirect {

    @Before
    public void setup() {
      pisAuth = mock(PisAuthorization.class);
      PisCommonPaymentData payment = mock(PisCommonPaymentData.class);
      List<PisAuthorization> authList = Collections.singletonList(pisAuth);

      when(pisAuth.getPaymentData()).thenReturn(payment);
      when(payment.getPaymentId()).thenReturn("1234");
      when(payment.getAuthorizations()).thenReturn(authList);
      when(payment.getTppInfo()).thenReturn(tppInfo);
      when(pisAuthorizationRepository.findByExternalId(anyString()))
          .thenReturn(Optional.of(pisAuth));
      when(pisCommonPaymentDataRepository.findByPaymentId(anyString()))
          .thenReturn(Optional.of(payment));
    }

    @Test
    public void shouldReturnUnsuccessfulScaUri() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(pisAuth.getPaymentData().getTransactionStatus()).thenReturn(TransactionStatus.RJCT);
      when(pisAuth.getTppNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(pisAuth.getTppOkRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingData(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/unsuccessfulsca", data.getTppRedirectUri());
      assertEquals("Rejected", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulScaUri() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      when(pisAuth.getPaymentData().getTransactionStatus()).thenReturn(TransactionStatus.ACSC);
      when(pisAuth.getTppNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(pisAuth.getTppOkRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);

      OnlineBankingData data = redirectService.getOnlineBankingData(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("AcceptedSettlementCompleted", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulUriWhenNokNotUriSet() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(pisAuth.getPaymentData().getTransactionStatus()).thenReturn(TransactionStatus.RJCT);
      when(pisAuth.getTppNokRedirectUri()).thenReturn(null);
      when(pisAuth.getTppOkRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingData(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("Rejected", data.getResourceStatus());
    }

    @Test
    public void shouldReturnScaHasFailedErrorPage() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      TestPsu psu = mock(TestPsu.class);
      when(testDataService.getPsu(anyString())).thenReturn(Optional.of(psu));
      try {
        redirectService.handlePaymentRedirectRequest(EXTERNAL_ID, PSU_ID, ScaOperation.INIT);
        fail("Expected exception not thrown");
      } catch (Exception e) {
        assertEquals("redirect-uri already called",
            e.getMessage());
      }
    }
  }

  public class Common {

    @Test
    public void shouldAllowExecutionOfAuth() {
      TestPsu scaPsu = mock(TestPsu.class);
      List<String> authIban = Collections.singletonList("DE17012013");

      when(testDataService.getPsuByIban(anyString())).thenReturn(Optional.of(scaPsu));
      assertTrue(redirectService.isPsuAllowedToExecuteAuth(Optional.of(scaPsu), authIban));
    }

    @Test
    public void shouldForbidExecutionOfAuthForNotPsusIban() {
      TestPsu scaPsu = mock(TestPsu.class);
      TestPsu otherPsu = mock(TestPsu.class);
      List<String> authIban = Collections.singletonList("DE17012013");

      when(testDataService.getPsuByIban(anyString())).thenReturn(Optional.of(otherPsu));
      assertFalse(redirectService.isPsuAllowedToExecuteAuth(Optional.of(scaPsu), authIban));
    }
  }
}
