package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.xs2a.core.error.MessageErrorCode;
import de.adorsys.psd2.xs2a.core.error.TppMessage;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import de.adorsys.psd2.xs2a.exception.RestException;
import de.adorsys.psd2.xs2a.spi.domain.authorisation.SpiScaConfirmation;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiGetPaymentStatusResponse;
import de.adorsys.psd2.xs2a.spi.domain.payment.response.SpiPaymentExecutionResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.service.SpiPayment;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

class AbstractPaymentSpiImpl {


  @Autowired
  PisPaymentDataRepository paymentDataRepository;

  SpiResponse<SpiGetPaymentStatusResponse> getPaymentStatusById(
      SpiSinglePayment payment) {

    Optional<TransactionStatus> paymentStatus = getPaymentStatusFromRepo(payment.getPaymentId());
    if (paymentStatus.isPresent()) {
      payment.setPaymentStatus(paymentStatus.get());
      return SpiResponse.<SpiGetPaymentStatusResponse>builder()
                 .payload(new SpiGetPaymentStatusResponse(payment.getPaymentStatus(),true))
                 .build();
    }
    return SpiResponse.<SpiGetPaymentStatusResponse>builder()
               .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_PAYMENT_NOT_FOUND))
               .build();
  }

  <T extends SpiSinglePayment> SpiResponse<T> getPaymentById(T payment) {
    Optional<TransactionStatus> paymentStatus = getPaymentStatusFromRepo(payment.getPaymentId());
    if (paymentStatus.isPresent()) {
      payment.setPaymentStatus(paymentStatus.get());
      return SpiResponse.<T>builder()
                 .payload(payment)
                 .build();
    }
    return SpiResponse.<T>builder()
               .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_PAYMENT_NOT_FOUND))
               .build();
  }

  SpiResponse<SpiPaymentExecutionResponse> checkTanAndSetStatusOfPayment(
      SpiPayment spiPayment,
      SpiScaConfirmation spiScaConfirmation) {
    if (spiScaConfirmation.getTanNumber().equals(TestDataService.GLOBAL_TAN)) {
      Optional<List<PisPaymentData>> paymentDataList = paymentDataRepository
          .findByPaymentId(spiPayment.getPaymentId());

      if (paymentDataList.isPresent()) {
        PisPaymentData payment = paymentDataList.get().get(0);
        payment.getPaymentData().setTransactionStatus(TransactionStatus.ACCP);
        paymentDataRepository.save(payment);
        return SpiResponse.<SpiPaymentExecutionResponse>builder()
                   .payload(new SpiPaymentExecutionResponse(TransactionStatus.ACCP))
                   .build();
      }

      return SpiResponse.<SpiPaymentExecutionResponse>builder()
             .error(new TppMessage(MessageErrorCode.FORMAT_ERROR_PAYMENT_NOT_FOUND,
                 "Payment not found"))
             .build();
    }

    return SpiResponse.<SpiPaymentExecutionResponse>builder()
               .error(new TppMessage(MessageErrorCode.PSU_CREDENTIALS_INVALID,"Wrong PIN"))
               .build();
  }

  void isCorrectCurrency(Optional<Account> account, SpiSinglePayment payment) {
    if (account.isPresent()) {
      Currency expectedCurrency = account.get().getCurrency();
      if (isIncorrectCurrency(payment, expectedCurrency)) {
        throw new RestException(MessageErrorCode.FORMAT_ERROR, "Account mismatch");
      }
    }
  }

  private boolean isIncorrectCurrency(SpiSinglePayment payment, Currency expectedCurrency) {
    return !(expectedCurrency.equals(payment.getDebtorAccount().getCurrency())
        && expectedCurrency.equals(payment.getInstructedAmount().getCurrency())
        && expectedCurrency.equals(payment.getCreditorAccount().getCurrency()));
  }

  private Optional<TransactionStatus> getPaymentStatusFromRepo(String paymentId) {
    Optional<List<PisPaymentData>> paymentData = paymentDataRepository
                                                     .findByPaymentId(paymentId);

    return paymentData.map(pisPaymentData -> TransactionStatus
        .valueOf(pisPaymentData.get(0).getPaymentData().getTransactionStatus().name()));
  }
}
