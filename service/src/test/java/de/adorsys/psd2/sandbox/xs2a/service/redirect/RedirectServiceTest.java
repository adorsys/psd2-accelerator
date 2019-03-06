package de.adorsys.psd2.sandbox.xs2a.service.redirect;

import static org.junit.Assert.assertEquals;
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
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
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
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingDataForConsent(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/unsuccessfulsca", data.getTppRedirectUri());
      assertEquals("rejected", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulScaUri() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      when(aisAuth.getConsent().getConsentStatus()).thenReturn(ConsentStatus.VALID);
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingDataForConsent(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("valid", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulUriWhenNokNotUriSet() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(aisAuth.getConsent().getConsentStatus()).thenReturn(ConsentStatus.REJECTED);
      when(tppInfo.getNokRedirectUri()).thenReturn(null);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingDataForConsent(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("rejected", data.getResourceStatus());
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
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingData(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/unsuccessfulsca", data.getTppRedirectUri());
      assertEquals("Rejected", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulScaUri() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      when(pisAuth.getPaymentData().getTransactionStatus()).thenReturn(TransactionStatus.ACSP);
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);

      OnlineBankingData data = redirectService.getOnlineBankingData(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("AcceptedSettlementInProcess", data.getResourceStatus());
    }

    @Test
    public void shouldReturnSuccessfulUriWhenNokNotUriSet() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(pisAuth.getPaymentData().getTransactionStatus()).thenReturn(TransactionStatus.RJCT);
      when(tppInfo.getNokRedirectUri()).thenReturn(null);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      OnlineBankingData data = redirectService.getOnlineBankingData(EXTERNAL_ID).get();

      assertEquals("http://tpp.de/success", data.getTppRedirectUri());
      assertEquals("Rejected", data.getResourceStatus());
    }
  }
}
