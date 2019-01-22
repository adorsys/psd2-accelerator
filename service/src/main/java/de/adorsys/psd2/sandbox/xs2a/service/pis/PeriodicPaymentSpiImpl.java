package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPeriodicPaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PeriodicPaymentSpi;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PeriodicPaymentSpiImpl extends AbstractPaymentSpiImpl implements PeriodicPaymentSpi {

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> executePaymentWithoutSca(
      @NotNull SpiContextData ctx,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return SpiResponse.<SpiResponse.VoidResponse>builder().success();
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiContextData ctx,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull AspspConsentData aspspConsentData) {

    return super.checkTanAndSetStatusOfPayment(
        spiPeriodicPayment,
        spiScaConfirmation,
        aspspConsentData
    );
  }

  @Override
  public @NotNull SpiResponse<SpiPeriodicPayment> getPaymentById(
      @NotNull SpiContextData contextData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return super.getPaymentById(contextData.getPsuData(), payment, aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiPeriodicPaymentInitiationResponse> initiatePayment(
      @NotNull SpiContextData contextData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull AspspConsentData initialAspspConsentData) {

    SpiPeriodicPaymentInitiationResponse response = new SpiPeriodicPaymentInitiationResponse();
    response.setTransactionStatus(SpiTransactionStatus.RCVD);
    String paymentId = UUID.randomUUID().toString();
    payment.setPaymentId(paymentId);
    response.setPaymentId(paymentId);

    return new SpiResponse<>(response, initialAspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiTransactionStatus> getPaymentStatusById(
      @NotNull SpiContextData contextData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return super.getPaymentStatusById(payment, aspspConsentData);
  }
}
