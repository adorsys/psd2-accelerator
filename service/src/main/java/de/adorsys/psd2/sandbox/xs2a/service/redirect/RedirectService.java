package de.adorsys.psd2.sandbox.xs2a.service.redirect;

import de.adorsys.psd2.consent.api.TypeAccess;
import de.adorsys.psd2.consent.domain.PsuData;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.domain.account.AisConsent;
import de.adorsys.psd2.consent.domain.account.AisConsentAuthorization;
import de.adorsys.psd2.consent.domain.account.TppAccountAccess;
import de.adorsys.psd2.consent.domain.payment.PisAuthorization;
import de.adorsys.psd2.consent.domain.payment.PisCommonPaymentData;
import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.AisConsentAuthorisationRepository;
import de.adorsys.psd2.consent.repository.PisAuthorisationRepository;
import de.adorsys.psd2.consent.repository.PisCommonPaymentDataRepository;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.BalanceType;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import de.adorsys.psd2.xs2a.core.sca.ScaStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RedirectService {

  private PisAuthorisationRepository pisAuthorizationRepository;
  private AisConsentAuthorisationRepository aisConsentAuthorizationRepository;
  private PisCommonPaymentDataRepository pisCommonPaymentDataRepository;
  private TestDataService testDataService;

  /**
   * Create a new RedirectService instance.
   *
   * @param pisAuthorizationRepository        PisAuthorizationRepository
   * @param aisConsentAuthorizationRepository AisConsentAuthorizationRepository
   * @param testDataService                   TestDataService
   */
  public RedirectService(PisAuthorisationRepository pisAuthorizationRepository,
      AisConsentAuthorisationRepository aisConsentAuthorizationRepository,
      PisCommonPaymentDataRepository pisCommonPaymentDataRepository,
      TestDataService testDataService) {
    this.pisAuthorizationRepository = pisAuthorizationRepository;
    this.aisConsentAuthorizationRepository = aisConsentAuthorizationRepository;
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

    AisConsent consent = aisConsentAuth.getConsent();

    Optional<TestPsu> psu = testDataService.getPsu(psuId);

    Optional<ConsentStatus> consentStatus;
    ScaStatus newScaStatus;
    if (psu.isPresent()) {
      if (consent.getAisConsentRequestType().equals(AisConsentRequestType.BANK_OFFERED)
          && testDataService.isSucccessfulPsu(psuId)) {
        consent.setAccesses(fillAccountAccesses());
      }
      consentStatus = ConsentStatus.fromValue(psu.get().getConsentStatusAfterSca().xs2aValue());
      newScaStatus = ScaStatus.fromValue(psu.get().getInitiationScaStatus().xs2aValue());
    } else {
      consentStatus = ConsentStatus.fromValue("rejected");
      newScaStatus = ScaStatus.FAILED;
    }
    consentStatus.ifPresent(consent::setConsentStatus);
    aisConsentAuth.setScaStatus(newScaStatus);
    aisConsentAuthorizationRepository.save(aisConsentAuth);
  }

  private TppAccountAccess getTppAccountAccess(Account account, TypeAccess typeAccess) {
    TppAccountAccess accountAccess = new TppAccountAccess();
    accountAccess.setAccountIdentifier(account.getIban());
    accountAccess.setCurrency(account.getCurrency());
    accountAccess.setTypeAccess(typeAccess);
    accountAccess.setAccountReferenceType(AccountReferenceType.IBAN);
    return accountAccess;
  }

  private List<TppAccountAccess> fillAccountAccesses() {
    List<Account> accountList = testDataService.getAccountsForBankOfferedConsent();
    List<TppAccountAccess> accountAccessList = new ArrayList<>();
    accountList.forEach(account -> {
      accountAccessList.add(getTppAccountAccess(account, TypeAccess.ACCOUNT));
      accountAccessList.add(getTppAccountAccess(account, TypeAccess.BALANCE));
      accountAccessList.add(getTppAccountAccess(account, TypeAccess.TRANSACTION));
    });
    return accountAccessList;
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

    if (!pisAuthorization.isPresent()) {
      //TODO handle error case
      return;
    }

    PisAuthorization paymentAuth = pisAuthorization.get();

    List<PisPaymentData> pisPaymentDataList = paymentAuth.getPaymentData().getPayments();

    if (pisPaymentDataList.isEmpty()) {
      //TODO handle error case
      return;
    }
    PisPaymentData pisPaymentData = pisPaymentDataList.get(0);

    Optional<TestPsu> psu = testDataService.getPsu(psuId);

    // TODO clean up optional handling (rat iio)
    if (isPsuAllowedToAccessPayment(psu, pisPaymentData.getDebtorAccount().getIban())) {
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

        if (!isPaymentAmountCoveredByBalance(pisPaymentData.getAmount(), psuId,
            pisPaymentData.getDebtorAccount().getIban())) {
          newTxStatus = TransactionStatus.RJCT;
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
    } else {
      TransactionStatus newTxStatus = TransactionStatus.RJCT;
      pisPaymentData.setTransactionStatus(newTxStatus);
      paymentAuth.getPaymentData().setTransactionStatus(newTxStatus);
      paymentAuth.setScaStatus(ScaStatus.FAILED);
    }
    pisAuthorizationRepository.save(paymentAuth);
  }

  private boolean isPaymentAmountCoveredByBalance(BigDecimal amount, String psuId, String iban) {
    Account account = testDataService.getRequestedAccounts(psuId, Arrays.asList(iban)).get().get(0);

    Balance accountBalance = account.getBalances().stream()
        .filter(balance -> balance.getBalanceType().equals(BalanceType.INTERIM_AVAILABLE))
        .findFirst().get();

    return accountBalance.getBalanceAmount().getAmount().compareTo(amount) >= 0;
  }

  private boolean isPsuAllowedToAccessPayment(Optional<TestPsu> scaPsu, String debtorIban) {
    Optional<TestPsu> accPsu = testDataService.getPsuByIban(debtorIban);
    if (!accPsu.isPresent() || !scaPsu.isPresent()) {
      return false;
    }
    return scaPsu.equals(accPsu);
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

    ScaStatus scaStatus = commonPaymentData.get().getAuthorizations().get(0).getScaStatus();

    if (scaStatus.equals(ScaStatus.FAILED)) {
      String nokRedirectUri = tppInfo.getNokRedirectUri();
      return nokRedirectUri == null ? tppInfo.getRedirectUri() : nokRedirectUri;
    }
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

    if (aisAuthorisation.get().getScaStatus().equals(ScaStatus.FAILED)) {
      String nokRedirectUri = tppInfo.getNokRedirectUri();
      return nokRedirectUri == null ? tppInfo.getRedirectUri() : nokRedirectUri;
    }

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
