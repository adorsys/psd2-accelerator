package de.adorsys.psd2.sandbox.xs2a.service.redirect;

import de.adorsys.psd2.consent.domain.PsuData;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.domain.account.AisConsent;
import de.adorsys.psd2.consent.domain.account.AisConsentAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.AisConsentAuthorisationRepository;
import de.adorsys.psd2.consent.repository.AisConsentRepository;
import de.adorsys.psd2.consent.repository.PisAuthorisationRepository;
import de.adorsys.psd2.consent.repository.PisCommonPaymentDataRepository;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.sandbox.portal.testdata.domain.TestPsu;
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

  private PisPaymentDataRepository pisPaymentDataRepository;
  private PisAuthorisationRepository pisAuthorizationRepository;
  private AisConsentAuthorisationRepository aisConsentAuthorizationRepository;
  private AisConsentRepository aisConsentRepository;
  private PisCommonPaymentDataRepository pisCommonPaymentDataRepository;
  private TestDataService testDataService;

  /**
   * Create a new RedirectService instance.
   *
   * @param pisPaymentDataRepository          PisPaymentRepository
   * @param pisAuthorizationRepository        PisAuthorizationRepository
   * @param aisConsentAuthorizationRepository AisConsentAuthorizationRepository
   * @param aisConsentRepository              AisConsentRepository
   * @param testDataService                   TestDataService
   */
  public RedirectService(PisPaymentDataRepository pisPaymentDataRepository,
      PisAuthorisationRepository pisAuthorizationRepository,
      AisConsentAuthorisationRepository aisConsentAuthorizationRepository,
      AisConsentRepository aisConsentRepository,
      PisCommonPaymentDataRepository pisCommonPaymentDataRepository,
      TestDataService testDataService) {
    this.pisPaymentDataRepository = pisPaymentDataRepository;
    this.pisAuthorizationRepository = pisAuthorizationRepository;
    this.aisConsentAuthorizationRepository = aisConsentAuthorizationRepository;
    this.aisConsentRepository = aisConsentRepository;
    this.testDataService = testDataService;
    this.pisCommonPaymentDataRepository = pisCommonPaymentDataRepository;
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

    if (!aisConsentAuthorization.isPresent()) {
      //TODO handle error case
      return;
    }
    AisConsentAuthorization aisConsentAuth = aisConsentAuthorization.get();

    AisConsent consent = aisConsentRepository.findOne(aisConsentAuth.getId());

    Optional<TestPsu> psu = testDataService.getPsu(psuId);
    if (psu.isPresent()) {
      Optional<ConsentStatus> consentStatus = ConsentStatus.fromValue(psu.get()
          .getConsentStatusAfterSca().xs2aValue());
      consentStatus.ifPresent(consent::setConsentStatus);
      ScaStatus newScaStatus = ScaStatus.fromValue(psu.get().getInitiationScaStatus().xs2aValue());
      aisConsentAuth.setScaStatus(newScaStatus);

    }
    aisConsentRepository.save(consent);
    aisConsentAuthorizationRepository.save(aisConsentAuth);
  }

  /**
   * Sets payment status in CMS depending on PSU-ID.
   *
   * @param externalId   Payment Id
   * @param psuId        Psu Id
   * @param scaOperation Executed for Initiation or Cancellation
   */
  public void handlePaymentRedirectRequest(String externalId, String psuId,
      ScaOperation scaOperation) {
    Optional<PisAuthorization> pisAuthorization = pisAuthorizationRepository
        .findByExternalId(externalId);

    if (!pisAuthorization.isPresent() || !testDataService.getPsu(psuId).isPresent()) {
      //TODO handle error case
      return;
    }

    PisAuthorization paymentAuth = pisAuthorization.get();

    Optional<List<PisPaymentData>> pisPaymentDataList = pisPaymentDataRepository
        .findByPaymentId(paymentAuth.getPaymentData().getPaymentId());

    if (!pisPaymentDataList.isPresent() || pisPaymentDataList.get().isEmpty()) {
      //TODO handle error case
      return;
    }
    PisPaymentData pisPaymentData = pisPaymentDataList.get().get(0);

    Optional<TestPsu> psu = testDataService.getPsu(psuId);

    if (psu.isPresent()) {
      if (scaOperation == ScaOperation.CANCEL) {
        TransactionStatus newTxStatus = TransactionStatus.getByValue(
            psu.get().getTransactionStatusAfterCancellation().xs2aValue()
        );
        pisPaymentData.setTransactionStatus(newTxStatus);
        paymentAuth.getPaymentData().setTransactionStatus(newTxStatus);
        ScaStatus newScaStatus = ScaStatus
            .fromValue(psu.get().getCancellationScaStatus().xs2aValue());
        paymentAuth.setScaStatus(newScaStatus);
      } else {
        TransactionStatus newTxStatus = TransactionStatus
            .getByValue(psu.get().getTransactionStatusAfterSca().xs2aValue());

        if (newTxStatus.equals(TransactionStatus.ACSC)
            && isFutureOrPeriodicPayment(pisPaymentData)) {
          newTxStatus = TransactionStatus.ACTC;
        }
        pisPaymentData.setTransactionStatus(newTxStatus);
        paymentAuth.getPaymentData().setTransactionStatus(newTxStatus);

        ScaStatus newScaStatus = ScaStatus
            .fromValue(psu.get().getInitiationScaStatus().xs2aValue());
        if (newScaStatus.isFinalisedStatus()) {
          // We need to update the payment with the PSU so that cancellation works. This happens
          // automatically in the embedded approach (rat).
          PsuData psuData = new PsuData();
          psuData.setPsuId(psuId);
          pisPaymentData.getPaymentData().getPsuData().add(psuData);
        }

        paymentAuth.setScaStatus(newScaStatus);
      }
      pisAuthorizationRepository.save(paymentAuth);
      pisPaymentDataRepository.save(pisPaymentData);
      pisCommonPaymentDataRepository.save(pisPaymentData.getPaymentData());
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

    Optional<PisCommonPaymentData> commonPaymentData = pisCommonPaymentDataRepository
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
   * @param externalId External consent Id
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

  /**
   * Returns the payment status to a specific payment.
   *
   * @param externalId External payment Id
   * @return payment status
   */
  public String getPaymentStatusFromRepo(String externalId) {
    Optional<PisAuthorization> pisAuthorization = pisAuthorizationRepository
        .findByExternalId(externalId);
    if (!pisAuthorization.isPresent()) {
      //TODO handle error case
      return null;
    }

    TransactionStatus transactionStatus = pisAuthorizationRepository.findByExternalId(externalId)
        .get().getPaymentData().getTransactionStatus();

    return transactionStatus.getTransactionStatus();
  }

  /**
   * Returns the consent status after SCA to a specific consent.
   *
   * @param externalId External consent Id
   * @return consent status
   */
  public String getConsentStatusFromRepo(String externalId) {
    Optional<AisConsentAuthorization> aisAuthorisation = aisConsentAuthorizationRepository
        .findByExternalId(externalId);
    if (!aisAuthorisation.isPresent()) {
      //TODO handle error case
      return null;
    }

    ConsentStatus consentStatus = aisAuthorisation.get().getConsent().getConsentStatus();

    return consentStatus.getValue();
  }

  private boolean isFutureOrPeriodicPayment(PisPaymentData pisPaymentData) {
    return pisPaymentData.getRequestedExecutionDate() != null
        || pisPaymentData.getStartDate() != null;
  }
}
