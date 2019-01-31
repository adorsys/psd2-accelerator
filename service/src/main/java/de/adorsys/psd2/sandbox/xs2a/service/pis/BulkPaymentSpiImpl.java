package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.SpiContextData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiBulkPayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiBulkPaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPaymentExecutionResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.BulkPaymentSpi;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class BulkPaymentSpiImpl implements BulkPaymentSpi {

  @Override
  public @NotNull SpiResponse<SpiBulkPaymentInitiationResponse> initiatePayment(
      @NotNull SpiContextData contextData,
      @NotNull SpiBulkPayment payment,
      @NotNull AspspConsentData initialAspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiBulkPayment> getPaymentById(@NotNull SpiContextData contextData,
      @NotNull SpiBulkPayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiTransactionStatus> getPaymentStatusById(
      @NotNull SpiContextData contextData,
      @NotNull SpiBulkPayment payment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiPaymentExecutionResponse> executePaymentWithoutSca(
      @NotNull SpiContextData spiContextData,
      @NotNull SpiBulkPayment spiBulkPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }

  @Override
  public @NotNull SpiResponse<SpiPaymentExecutionResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiContextData spiContextData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiBulkPayment spiBulkPayment,
      @NotNull AspspConsentData aspspConsentData) {
    return null;
  }
}
