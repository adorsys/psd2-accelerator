package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiSinglePaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.SinglePaymentSpi;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SinglePaymentSpiImpl extends AbstractPaymentSpiImpl implements SinglePaymentSpi {

  @Override
  public @NotNull SpiResponse<SpiSinglePaymentInitiationResponse> initiatePayment(
      @NotNull SpiContextData ctx,
      @NotNull SpiSinglePayment payment,
      @NotNull AspspConsentData initialAspspConsentData) {
    SpiSinglePaymentInitiationResponse response = new SpiSinglePaymentInitiationResponse();
    response.setTransactionStatus(SpiTransactionStatus.RCVD);

    String paymentId = UUID.randomUUID().toString();
    payment.setPaymentId(paymentId);
    response.setPaymentId(paymentId);

    return new SpiResponse<>(response, initialAspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> executePaymentWithoutSca(
      @NotNull SpiContextData ctx,
      @NotNull SpiSinglePayment spiSinglePayment,
      @NotNull AspspConsentData aspspConsentData) {
    return SpiResponse.<SpiResponse.VoidResponse>builder().success();
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiContextData ctx,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiSinglePayment spiSinglePayment,
      @NotNull AspspConsentData aspspConsentData) {

    return super.checkTanAndSetStatusOfPayment(
        spiSinglePayment,
        spiScaConfirmation,
        aspspConsentData
    );
  }

  @Override
  public @NotNull SpiResponse<SpiSinglePayment> getPaymentById(
      @NotNull SpiContextData ctx,
      @NotNull SpiSinglePayment payment,
      @NotNull AspspConsentData aspspConsentData) {

    return super.getPaymentById(ctx.getPsuData(), payment, aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiTransactionStatus> getPaymentStatusById(
      @NotNull SpiContextData contextData, @NotNull SpiSinglePayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return super.getPaymentStatusById(payment, aspspConsentData);
  }
}
