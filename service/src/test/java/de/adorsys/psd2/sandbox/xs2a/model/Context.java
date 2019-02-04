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
  private String accountId;
  private String authorisationId;
  private String cancellationId;
  private String scaRedirect;
  private String consentId;
  private AccountAccess consentAccountAccess;
  private ResponseEntity actualResponse;

  @SuppressWarnings("unchecked") // "just" test code
  public <T> ResponseEntity<T> getActualResponse() {
    return (ResponseEntity<T>) actualResponse;
  }
}
