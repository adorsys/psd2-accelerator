package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
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
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import de.adorsys.psd2.xs2a.spi.service.PaymentCancellationSpi;
import de.adorsys.psd2.xs2a.spi.service.SpiPayment;
import java.util.Collections;
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
      @NotNull AspspConsentData aspspConsentData) {
    SpiPaymentCancellationResponse cancellationResponse = new SpiPaymentCancellationResponse();
    cancellationResponse.setCancellationAuthorisationMandated(true);
    cancellationResponse.setTransactionStatus(TransactionStatus.valueOf(
        paymentDataRepository.findByPaymentId(spiPayment.getPaymentId()).get().get(0)
            .getPaymentData().getTransactionStatus().name()));
    return new SpiResponse<>(cancellationResponse, aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> cancelPaymentWithoutSca(
      @NotNull SpiContextData ctx,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndCancelPayment(
      @NotNull SpiContextData ctx,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {

    if (spiScaConfirmation.getTanNumber().equals(TestDataService.GLOBAL_TAN)) {
      Optional<List<PisPaymentData>> paymentDataList = paymentDataRepository
          .findByPaymentId(spiPayment.getPaymentId());

      if (paymentDataList.isPresent()) {
        PisPaymentData payment = paymentDataList.get().get(0);
        payment.getPaymentData().setTransactionStatus(TransactionStatus.CANC);
        paymentDataRepository.save(payment);
        return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
      }

      return SpiResponse.<SpiResponse.VoidResponse>builder()
          .aspspConsentData(aspspConsentData)
          .message(Collections.singletonList("Payment not found"))
          .fail(SpiResponseStatus.LOGICAL_FAILURE);
    }

    return SpiResponse.<SpiResponse.VoidResponse>builder()
        .aspspConsentData(aspspConsentData)
        .message(Collections.singletonList("Wrong PIN"))
        .fail(SpiResponseStatus.UNAUTHORIZED_FAILURE);
  }

  @Override
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(@NotNull SpiContextData ctx,
      @NotNull SpiPsuData psuData,
      String password,
      SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    String iban = null;

    if (spiPayment instanceof SpiSinglePayment) {
      iban = ((SpiSinglePayment) spiPayment).getDebtorAccount().getIban();
    }
    if (spiPayment instanceof SpiPeriodicPayment) {
      iban = ((SpiPeriodicPayment) spiPayment).getDebtorAccount().getIban();
    }

    return authorisationService.authorisePsu(psuData, password, iban, aspspConsentData, true);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiContextData ctx,
      SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return authorisationService.requestAvailableScaMethods(aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiContextData ctx,
      @NotNull String s,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return authorisationService.requestAuthorisationCode(s, aspspConsentData);
  }
}
