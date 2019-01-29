package de.adorsys.psd2.sandbox.xs2a.service.redirect;

import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.domain.account.AisConsent;
import de.adorsys.psd2.consent.domain.account.AisConsentAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.AisConsentAuthorizationRepository;
import de.adorsys.psd2.consent.repository.AisConsentRepository;
import de.adorsys.psd2.consent.repository.PisAuthorizationRepository;
import de.adorsys.psd2.consent.repository.PisCommonPaymentDataRepository;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RedirectService {

  private TestDataService testDataService;
  private PisPaymentDataRepository pisPaymentDataRepository;
  private PisAuthorizationRepository pisAuthorizationRepository;
  private AisConsentAuthorizationRepository aisConsentAuthorizationRepository;
  private AisConsentRepository aisConsentRepository;
  private PisCommonPaymentDataRepository commonPaymentDataRepository;

  /**
   * Create a new RedirectService instance.
   *
   * @param pisPaymentDataRepository          PisPaymentRepository
   * @param pisAuthorizationRepository        PisAuthorizationRepository
   * @param aisConsentAuthorizationRepository AisConsentAuthorizationRepository
   * @param aisConsentRepository              AisConsentRepository
   */
  public RedirectService(TestDataService testDataService,
      PisPaymentDataRepository pisPaymentDataRepository,
      PisAuthorizationRepository pisAuthorizationRepository,
      AisConsentAuthorizationRepository aisConsentAuthorizationRepository,
      AisConsentRepository aisConsentRepository,
      PisCommonPaymentDataRepository commonPaymentDataRepository) {
    this.testDataService = testDataService;
    this.pisPaymentDataRepository = pisPaymentDataRepository;
    this.pisAuthorizationRepository = pisAuthorizationRepository;
    this.aisConsentAuthorizationRepository = aisConsentAuthorizationRepository;
    this.aisConsentRepository = aisConsentRepository;
    this.commonPaymentDataRepository = commonPaymentDataRepository;
  }

  /**
   * Sets consent status in CMS depending on PSU-ID.
   *
   * @param externalId Consent Id
   * @param psuId      Psu Id
   */
  public void handleConsentCreationRedirectRequest(String externalId, String psuId) {
    Optional<AisConsentAuthorization> aisConsentAuthorization = aisConsentAuthorizationRepository
        .findByExternalId(externalId);

    if (!aisConsentAuthorization.isPresent() || !testDataService.getPsu(psuId).isPresent()) {
      //TODO handle error case
      return;
    }
    AisConsentAuthorization aisConsentAuth = aisConsentAuthorization.get();

    AisConsent consent = aisConsentRepository.findOne(aisConsentAuth.getId());
    // TODO: Set status of PSU from TestDataService
    consent.setConsentStatus(ConsentStatus.VALID);
    aisConsentAuth.setScaStatus(ScaStatus.PSUAUTHENTICATED);

    aisConsentRepository.save(consent);
    aisConsentAuthorizationRepository.save(aisConsentAuth);
  }

  /**
   * Sets payment status in CMS depending on PSU-ID.
   *
   * @param externalId Payment Id
   * @param psuId      Psu Id
   */
  // TODO: handle isInit for cancellation and initiation scenarios
  public void handlePaymentRedirectRequest(String externalId, String psuId, boolean isInit) {
    Optional<PisAuthorization> pisAuthorization = pisAuthorizationRepository
        .findByExternalId(externalId);

    if (!pisAuthorization.isPresent() || !testDataService.getPsu(psuId).isPresent()) {
      //TODO handle error case
      return;
    }

    PisAuthorization payment = pisAuthorization.get();

    Optional<List<PisPaymentData>> pisPaymentDataList = pisPaymentDataRepository
        .findByPaymentId(payment.getPaymentData().getPaymentId());

    if (!pisPaymentDataList.isPresent() && pisPaymentDataList.get().isEmpty()) {
      //TODO handle error case
      return;
    }
    PisPaymentData pisPaymentData = pisPaymentDataList.get().get(0);

    pisPaymentData.setTransactionStatus(getTransactionStatus(psuId));

    // TODO: Set status of PSU from TestDataService
    payment.setScaStatus(ScaStatus.FINALISED);
    payment.getPaymentData().setTransactionStatus(getTransactionStatus(psuId));
    pisPaymentDataRepository.save(pisPaymentData);
  }

  private TransactionStatus getTransactionStatus(String psuId) {
    if (psuId.equalsIgnoreCase("psu-unknown")
        || psuId.equalsIgnoreCase("psu-rejected")) {
      return TransactionStatus.RCVD;
    } else {
      return TransactionStatus.ACCP;
    }
  }

  /**
   * Returns the tppRedirectUri for a specific payment.
   *
   * @param externalId External payment Id
   * @return tppRedirectUri
   */
  public String getRedirectToTppUriFromPaymentRepo(String externalId) {
    Optional<PisAuthorization> pisAuthorization = pisAuthorizationRepository
        .findByExternalId(externalId);
    if (!pisAuthorization.isPresent()) {
      //TODO handle error case
      return null;
    }

    Optional<PisCommonPaymentData> commonPaymentData = commonPaymentDataRepository
        .findByPaymentId(pisAuthorization.get().getPaymentData().getPaymentId());

    if (!commonPaymentData.isPresent()) {
      return null;
    }

    TppInfoEntity tppInfo = commonPaymentData.get().getAuthorizations().get(0).getPaymentData()
        .getTppInfo();

    return tppInfo.getRedirectUri();
  }

  /**
   * Returns the tppRedirectUri for a specific consent.
   *
   * @param externalId External payment Id
   * @return tppRedirectUri
   */
  public String getRedirectToTppUriFromAccountRepo(String externalId) {
    Optional<AisConsentAuthorization> aisAuthorisation = aisConsentAuthorizationRepository
        .findByExternalId(externalId);
    if (!aisAuthorisation.isPresent()) {
      //TODO handle error case
      return null;
    }

    TppInfoEntity tppInfo = aisAuthorisation.get().getConsent().getTppInfo();

    return tppInfo.getRedirectUri();
  }
}
