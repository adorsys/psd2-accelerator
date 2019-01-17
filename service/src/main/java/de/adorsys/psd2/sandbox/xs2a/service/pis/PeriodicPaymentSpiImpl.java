package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiPeriodicPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPeriodicPaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.PeriodicPaymentSpi;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PeriodicPaymentSpiImpl extends AbstractPaymentSpiImpl implements PeriodicPaymentSpi {

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> executePaymentWithoutSca(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiPeriodicPayment spiPeriodicPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return SpiResponse.<SpiResponse.VoidResponse>builder().success();
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiPsuData spiPsuData,
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
  public @NotNull SpiResponse<SpiPeriodicPaymentInitiationResponse> initiatePayment(
      @NotNull SpiPsuData psuData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull AspspConsentData initialAspspConsentData) {
    SpiPeriodicPaymentInitiationResponse response = new SpiPeriodicPaymentInitiationResponse();
    response.setTransactionStatus(SpiTransactionStatus.RCVD);
    return new SpiResponse<>(response, initialAspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiPeriodicPayment> getPaymentById(
      @NotNull SpiPsuData psuData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return super.getPaymentById(psuData, payment, aspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiTransactionStatus> getPaymentStatusById(
      @NotNull SpiPsuData psuData,
      @NotNull SpiPeriodicPayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return super.getPaymentStatusById(psuData, payment, aspspConsentData);
  }
}
