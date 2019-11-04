package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.core.error.TppMessage;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.SpiAspspConsentDataProvider;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPaymentCancellationResponse;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PaymentCancellationSpi;
import de.adorsys.psd2.xs2a.spi.service.SpiPayment;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PaymentCancellationSpiImpl implements PaymentCancellationSpi {

  private final PisPaymentDataRepository paymentDataRepository;
  private AuthorisationService authorisationService;

  public PaymentCancellationSpiImpl(
      PisPaymentDataRepository paymentDataRepository, AuthorisationService authorisationService) {
    this.paymentDataRepository = paymentDataRepository;
    this.authorisationService = authorisationService;
  }

  @Override
  public @NotNull SpiResponse<SpiPaymentCancellationResponse> initiatePaymentCancellation(
      @NotNull SpiContextData ctx,
      @NotNull SpiPayment spiPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    SpiPaymentCancellationResponse cancellationResponse = new SpiPaymentCancellationResponse();
    cancellationResponse.setCancellationAuthorisationMandated(true);
    cancellationResponse.setTransactionStatus(TransactionStatus.valueOf(
        paymentDataRepository.findByPaymentId(spiPayment.getPaymentId()).get().get(0)
            .getPaymentData().getTransactionStatus().name()));
    return SpiResponse.<SpiPaymentCancellationResponse>builder()
        .payload(cancellationResponse)
        .build();
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> cancelPaymentWithoutSca(
      @NotNull SpiContextData ctx,
      @NotNull SpiPayment spiPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return SpiResponse.<SpiResponse.VoidResponse>builder()
               .payload(SpiResponse.voidResponse())
               .build();
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndCancelPayment(
      @NotNull SpiContextData ctx,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPayment spiPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {

    if (spiScaConfirmation.getTanNumber().equals(TestDataService.GLOBAL_TAN)) {
      Optional<List<PisPaymentData>> paymentDataList = paymentDataRepository
          .findByPaymentId(spiPayment.getPaymentId());

      if (paymentDataList.isPresent()) {
        PisPaymentData payment = paymentDataList.get().get(0);
        payment.getPaymentData().setTransactionStatus(TransactionStatus.CANC);
        paymentDataRepository.save(payment);
        return SpiResponse.<SpiResponse.VoidResponse>builder()
                   .payload(SpiResponse.voidResponse())
                   .build();
      }

      return SpiResponse.<SpiResponse.VoidResponse>builder()
          .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_PAYMENT_NOT_FOUND,
              "Payment not found"))
          .build();
    }

    return SpiResponse.<SpiResponse.VoidResponse>builder()
        .error(new TppMessage(MessageErrorCode.UNAUTHORIZED,"Wrong PIN"))
        .build();
  }

  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(@NotNull SpiContextData ctx,
      @NotNull SpiPsuData psuData,
      String password,
      SpiPayment spiPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    String iban = null;

    if (spiPayment instanceof SpiSinglePayment) {
      iban = ((SpiSinglePayment) spiPayment).getDebtorAccount().getIban();
    }
    if (spiPayment instanceof SpiPeriodicPayment) {
      iban = ((SpiPeriodicPayment) spiPayment).getDebtorAccount().getIban();
    }

    return authorisationService.authorisePsu(psuData, password, iban, true);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiContextData ctx,
      SpiPayment spiPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return authorisationService.requestAvailableScaMethods();
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiContextData ctx,
      @NotNull String s,
      @NotNull SpiPayment spiPayment,
      @NotNull SpiAspspConsentDataProvider spiAspspConsentDataProvider) {
    return authorisationService.requestAuthorisationCode(s);
  }
}
