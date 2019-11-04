package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.exception.RestException;
import de.adorsys.psd2.xs2a.spi.domain.SpiAspspConsentDataProvider;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiGetPaymentStatusResponse;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPaymentExecutionResponse;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPeriodicPaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PeriodicPaymentSpi;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PeriodicPaymentSpiImpl extends AbstractPaymentSpiImpl implements PeriodicPaymentSpi {

  private TestDataService testDataService;

  public PeriodicPaymentSpiImpl(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  @Override
  public @NotNull SpiResponse<SpiPaymentExecutionResponse> executePaymentWithoutSca(
      @NotNull SpiContextData ctx,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return SpiResponse.<SpiPaymentExecutionResponse>builder().build();
  }

  @Override
  public @NotNull SpiResponse<SpiPaymentExecutionResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiContextData ctx,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    return super.checkTanAndSetStatusOfPayment(
        spiPeriodicPayment,
        spiScaConfirmation
    );
  }

  @Override
  public @NotNull SpiResponse<SpiPeriodicPayment> getPaymentById(
      @NotNull SpiContextData contextData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return super.getPaymentById(payment);
  }

  @Override
  public @NotNull SpiResponse<SpiPeriodicPaymentInitiationResponse> initiatePayment(
      @NotNull SpiContextData contextData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    Optional<TestPsu> psuId = testDataService.getPsuByIban(payment.getDebtorAccount().getIban());
    // TODO what should we do here? (e.g. no account with this IBAN exists)
    TestPsu knownPsu = psuId.orElseThrow(() -> new RestException(MessageErrorCode.PAYMENT_FAILED,
        "User not found"));

    if (testDataService.isBlockedPsu(knownPsu.getPsuId())) {
      throw new RestException(MessageErrorCode.SERVICE_BLOCKED, "User is BLOCKED by ASPSP");
    }

    Optional<Account> account = testDataService.getAccountByIban(psuId.get().getPsuId(),
        payment.getDebtorAccount().getIban());
    super.isCorrectCurrency(account, payment);

    SpiPeriodicPaymentInitiationResponse response = new SpiPeriodicPaymentInitiationResponse();
    response.setTransactionStatus(TransactionStatus.RCVD);
    String paymentId = UUID.randomUUID().toString();
    payment.setPaymentId(paymentId);
    response.setPaymentId(paymentId);

    return SpiResponse.<SpiPeriodicPaymentInitiationResponse>builder()
        .payload(response)
        .build();
  }

  @Override
  public @NotNull SpiResponse<SpiGetPaymentStatusResponse> getPaymentStatusById(
      @NotNull SpiContextData contextData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return super.getPaymentStatusById(payment);
  }
}
