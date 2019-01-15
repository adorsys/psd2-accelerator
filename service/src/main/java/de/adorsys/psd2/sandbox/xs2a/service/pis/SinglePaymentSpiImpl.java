package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiSinglePaymentInitiationResponse;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import de.adorsys.psd2.xs2a.spi.service.SinglePaymentSpi;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SinglePaymentSpiImpl extends AbstractPaymentSpiImpl implements SinglePaymentSpi {

  @Autowired
  PisPaymentDataRepository paymentDataRepository;

  private static final String MOCKED_TAN = "54321";

  @Override
  public @NotNull SpiResponse<SpiSinglePaymentInitiationResponse> initiatePayment(
      @NotNull SpiPsuData psuData,
      @NotNull SpiSinglePayment payment,
      @NotNull AspspConsentData initialAspspConsentData) {
    SpiSinglePaymentInitiationResponse response = new SpiSinglePaymentInitiationResponse();
    response.setTransactionStatus(SpiTransactionStatus.RCVD);
    return new SpiResponse<>(response, initialAspspConsentData);
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> executePaymentWithoutSca(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiSinglePayment spiSinglePayment,
      @NotNull AspspConsentData aspspConsentData) {
    return SpiResponse.<SpiResponse.VoidResponse>builder().success();
  }

  @Override
  public @NotNull SpiResponse<SpiResponse.VoidResponse> verifyScaAuthorisationAndExecutePayment(
      @NotNull SpiPsuData spiPsuData,
      @NotNull SpiScaConfirmation spiScaConfirmation,
      @NotNull SpiSinglePayment spiSinglePayment,
      @NotNull AspspConsentData aspspConsentData) {

    if (spiScaConfirmation.getTanNumber().equals(MOCKED_TAN)) {
      return new SpiResponse<>(SpiResponse.voidResponse(), aspspConsentData);
    }

    return SpiResponse.<SpiResponse.VoidResponse>builder()
        .aspspConsentData(aspspConsentData)
        .payload(null)
        .message(Collections.singletonList("Wrong PIN"))
        .fail(SpiResponseStatus.UNAUTHORIZED_FAILURE);
  }

  @Override
  public @NotNull SpiResponse<SpiSinglePayment> getPaymentById(
      @NotNull SpiPsuData psuData,
      @NotNull SpiSinglePayment payment,
      @NotNull AspspConsentData aspspConsentData) {

    return super.getPaymentById(psuData, payment, aspspConsentData);

  }

  @Override
  public @NotNull SpiResponse<SpiTransactionStatus> getPaymentStatusById(
      @NotNull SpiPsuData psuData,
      @NotNull SpiSinglePayment payment,
      @NotNull AspspConsentData aspspConsentData) {

    return super.getPaymentStatusById(psuData, payment, aspspConsentData);
  }
}
