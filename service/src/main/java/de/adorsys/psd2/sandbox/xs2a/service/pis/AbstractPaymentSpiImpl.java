/*
 * Copyright 2019 adorsys GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.adorsys.psd2.sandbox.xs2a.service.pis;

import de.adorsys.psd2.consent.domain.payment.PisPaymentData;
import de.adorsys.psd2.consent.repository.PisPaymentDataRepository;
import de.adorsys.psd2.xs2a.core.consent.AspspConsentData;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiTransactionStatus;
import de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment;
import de.adorsys.psd2.xs2a.spi.domain.psu.SpiPsuData;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

class AbstractPaymentSpiImpl {

  @Autowired
  PisPaymentDataRepository paymentDataRepository;

  SpiResponse<SpiTransactionStatus> getPaymentStatusById(
      SpiPsuData psuData,
      SpiSinglePayment payment,
      AspspConsentData aspspConsentData) {

    Optional<SpiTransactionStatus> paymentStatus = getPaymentStatusFromRepo(payment.getPaymentId());
    if (paymentStatus.isPresent()) {
      payment.setPaymentStatus(paymentStatus.get());
      return SpiResponse.<SpiTransactionStatus>builder()
          .aspspConsentData(aspspConsentData)
          .payload(payment.getPaymentStatus())
          .success();
    }
    return SpiResponse.<SpiTransactionStatus>builder()
        .fail(SpiResponseStatus.LOGICAL_FAILURE);
  }

  SpiResponse<SpiSinglePayment> getSinglePaymentById(
      SpiSinglePayment payment,
      AspspConsentData aspspConsentData) {

    Optional<SpiTransactionStatus> paymentStatus = getPaymentStatusFromRepo(payment.getPaymentId());
    if (paymentStatus.isPresent()) {
      payment.setPaymentStatus(paymentStatus.get());
      return SpiResponse.<SpiSinglePayment>builder()
          .aspspConsentData(aspspConsentData)
          .payload(payment)
          .success();
    }
    return SpiResponse.<SpiSinglePayment>builder()
        .fail(SpiResponseStatus.LOGICAL_FAILURE);
  }

  private Optional<SpiTransactionStatus> getPaymentStatusFromRepo(String paymentId) {
    Optional<List<PisPaymentData>> paymentData = paymentDataRepository
        .findByPaymentId(paymentId);

    return paymentData.map(pisPaymentData -> SpiTransactionStatus
        .valueOf(pisPaymentData.get(0).getTransactionStatus().name()));
  }

}
