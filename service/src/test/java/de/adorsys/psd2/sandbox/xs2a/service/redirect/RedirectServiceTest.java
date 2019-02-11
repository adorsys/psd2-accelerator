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
  PisAuthorisationRepository pisAuthorizationRepository;

  @Mock
  AisConsentAuthorisationRepository aisConsentAuthorizationRepository;

  @Mock
  PisCommonPaymentDataRepository pisCommonPaymentDataRepository;

  @Mock
  TestDataService testDataService;

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
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      String url = redirectService.getRedirectToTppUriFromAccountRepo(EXTERNAL_ID);

      assertEquals(url, "http://tpp.de/unsuccessfulsca");
    }

    @Test
    public void shouldReturnSuccessfulScaUri() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      String url = redirectService.getRedirectToTppUriFromAccountRepo(EXTERNAL_ID);

      assertEquals(url, "http://tpp.de/success");
    }

    @Test
    public void shouldReturnSuccessfulUriWhenNokNotUriSet() {
      when(aisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(tppInfo.getNokRedirectUri()).thenReturn(null);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      String url = redirectService.getRedirectToTppUriFromAccountRepo(EXTERNAL_ID);

      assertEquals(url, "http://tpp.de/success");
    }
  }

  public class PisRedirect {

    @Before
    public void setup() {
      PisCommonPaymentData payment = mock(PisCommonPaymentData.class);
      pisAuth = mock(PisAuthorization.class);
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
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      String url = redirectService.getRedirectToTppUriFromPaymentRepo(EXTERNAL_ID);

      assertEquals(url, "http://tpp.de/unsuccessfulsca");
    }

    @Test
    public void shouldReturnSuccessfulScaUri() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FINALISED);
      when(tppInfo.getNokRedirectUri()).thenReturn(UNSUCCESSFUL_SCA_URI);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      String url = redirectService.getRedirectToTppUriFromPaymentRepo(EXTERNAL_ID);

      assertEquals(url, "http://tpp.de/success");
    }

    @Test
    public void shouldReturnSuccessfulUriWhenNokNotUriSet() {
      when(pisAuth.getScaStatus()).thenReturn(ScaStatus.FAILED);
      when(tppInfo.getNokRedirectUri()).thenReturn(null);
      when(tppInfo.getRedirectUri()).thenReturn(SUCCESSFUL_SCA_URI);
      String url = redirectService.getRedirectToTppUriFromPaymentRepo(EXTERNAL_ID);

      assertEquals(url, "http://tpp.de/success");
    }
  }
}
