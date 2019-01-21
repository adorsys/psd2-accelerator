package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.sandbox.portal.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.service.AuthorisationService;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthenticationObject;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorisationStatus;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiAuthorizationCodeResult;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
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
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    SpiPaymentCancellationResponse cancellationResponse = new SpiPaymentCancellationResponse();
    cancellationResponse.setCancellationAuthorisationMandated(true);
    cancellationResponse.setTransactionStatus(SpiTransactionStatus.valueOf(
        paymentDataRepository.findByPaymentId(spiPayment.getPaymentId()).get().get(0)
            .getTransactionStatus().name()));
    return new SpiResponse<>(cancellationResponse, aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> cancelPaymentWithoutSca(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndCancelPayment(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    if (spiScaConfirmation.getTanNumber().equals(TestDataService.TAN)) {
      Optional<List<PisPaymentData>> paymentDataList = paymentDataRepository
          .findByPaymentId(spiPayment.getPaymentId());

      if (paymentDataList.isPresent()) {
        PisPaymentData payment = paymentDataList.get().get(0);
        payment.setTransactionStatus(TransactionStatus.CANC);
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
  public SpiResponse<SpiAuthorisationStatus> authorisePsu(@NotNull SpiPsuData spiPsuData,
      String password, SpiPayment spiPayment, AspspConsentData aspspConsentData) {
    String iban = null;

    if (spiPayment instanceof SpiSinglePayment) {
      iban = ((SpiSinglePayment) spiPayment).getDebtorAccount().getIban();
    }
    if (spiPayment instanceof SpiPeriodicPayment) {
      iban = ((SpiPeriodicPayment) spiPayment).getDebtorAccount().getIban();
    }

    return authorisationService.authorisePsu(spiPsuData, password, iban, aspspConsentData);
  }

  @Override
  public SpiResponse<List<SpiAuthenticationObject>> requestAvailableScaMethods(
      @NotNull SpiPsuData spiPsuData, SpiPayment spiPayment,
      AspspConsentData aspspConsentData) {
    return authorisationService.requestAvailableScaMethods(aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiAuthorizationCodeResult> requestAuthorisationCode(
      @NotNull SpiPsuData spiPsuData, @NotNull String s,
      @NotNull SpiPayment spiPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return authorisationService.requestAuthorisationCode(s, aspspConsentData);
  }
}
