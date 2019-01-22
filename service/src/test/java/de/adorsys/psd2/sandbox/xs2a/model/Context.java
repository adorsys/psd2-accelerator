package de.adorsys.psd2.sandbox.xs2a.model;

import de.adorsys.psd2.model.AccountAccess;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public class Context {

  private String psuId;
  private String scaMethod;
  private String tanValue;
  private String paymentProduct;
  private String paymentService;
  private String paymentId;
  private String authorisationId;
  private String cancellationId;
  private String consentId;
  private AccountAccess consentAccountAccess;
  private ResponseEntity actualResponse;
}
