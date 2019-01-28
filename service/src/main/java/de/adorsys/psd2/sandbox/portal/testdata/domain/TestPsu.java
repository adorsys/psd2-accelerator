package de.adorsys.psd2.sandbox.portal.testdata.domain;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TestPsu {

  private String psuId;
  private String password;
  private String tan;
  private HashMap<String, Account> accounts;
  private String transactionStatusAfterSca;
  private String consentStatusAfterSca;
  private String initiationScaStatus;
  private String transactionStatusAfterCancellation;
  private String consentStatusAfterDeletion;
  private String cancellationScaStatus;
}
